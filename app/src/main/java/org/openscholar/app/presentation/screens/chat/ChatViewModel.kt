package org.openscholar.app.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.openscholar.app.data.local.dao.ChatDao
import org.openscholar.app.data.local.entity.ChatMessageEntity
import org.openscholar.app.data.local.entity.ChatSessionEntity
import org.openscholar.app.data.remote.ai.base.AIProviderManager
import org.openscholar.app.data.remote.ai.base.ChatMessage
import org.openscholar.app.data.remote.ai.base.GenerationOptions
import org.openscholar.app.domain.repository.DocumentRepository
import org.openscholar.app.model.MessageRole
import java.util.UUID
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatUiMessage> = emptyList(),
    val currentSessionId: String? = null,
    val inputText: String = "",
    val isProcessing: Boolean = false,
    val sessions: List<ChatSessionEntity> = emptyList()
)

data class ChatUiMessage(
    val id: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiProviderManager: AIProviderManager,
    private val documentRepository: DocumentRepository,
    private val chatDao: ChatDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
        createNewSession()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            chatDao.getAllSessions().collect { sessions ->
                _uiState.value = _uiState.value.copy(sessions = sessions)
            }
        }
    }

    fun createNewSession() {
        viewModelScope.launch {
            val session = ChatSessionEntity()
            chatDao.insertSession(session)
            _uiState.value = _uiState.value.copy(
                currentSessionId = session.id,
                messages = emptyList()
            )
        }
    }

    fun selectSession(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(currentSessionId = sessionId)
            chatDao.getMessagesBySession(sessionId).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    messages = messages.map { it.toUi() }
                )
            }
        }
    }

    fun setInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isProcessing) return

        val sessionId = _uiState.value.currentSessionId ?: return

        _uiState.value = _uiState.value.copy(inputText = "", isProcessing = true)

        val userMessage = ChatUiMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            content = text
        )
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage
        )

        viewModelScope.launch {
            // Save user message
            chatDao.insertMessage(
                ChatMessageEntity(
                    messageId = userMessage.id,
                    sessionId = sessionId,
                    role = MessageRole.USER,
                    content = text
                )
            )

            // Get context from documents
            val contextDocs = StringBuilder()
            documentRepository.getAllDocuments().collect { docs ->
                docs.take(3).forEach { doc ->
                    contextDocs.appendLine("[${doc.title}] ${doc.content.take(500)}")
                }
                return@collect
            }

            val systemPrompt = buildString {
                appendLine("You are OpenScholar, an AI research assistant.")
                appendLine("Answer questions based on the user's document library.")
                appendLine("Always cite sources when referencing specific documents.")
                appendLine()
                appendLine("User's document context:")
                appendLine(contextDocs.toString().ifEmpty { "No documents available yet." })
            }

            val messages = _uiState.value.messages.map { msg ->
                ChatMessage(msg.role.name.lowercase(), msg.content)
            }

            when (val result = aiProviderManager.chat(
                messages = messages,
                options = GenerationOptions(temperature = 0.7f, maxTokens = 2048)
            )) {
                is org.openscholar.app.data.remote.ai.base.AIResult.Success -> {
                    val assistantMessage = ChatUiMessage(
                        id = UUID.randomUUID().toString(),
                        role = MessageRole.ASSISTANT,
                        content = result.text
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + assistantMessage,
                        isProcessing = false
                    )
                    chatDao.insertMessage(
                        ChatMessageEntity(
                            messageId = assistantMessage.id,
                            sessionId = sessionId,
                            role = MessageRole.ASSISTANT,
                            content = result.text,
                            citations = "[]"
                        )
                    )
                }
                is org.openscholar.app.data.remote.ai.base.AIResult.Error -> {
                    val errorMessage = ChatUiMessage(
                        id = UUID.randomUUID().toString(),
                        role = MessageRole.ASSISTANT,
                        content = "I encountered an error: ${result.message}"
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + errorMessage,
                        isProcessing = false
                    )
                }
            }
        }
    }

    private fun ChatMessageEntity.toUi() = ChatUiMessage(
        id = messageId,
        role = role,
        content = content,
        timestamp = timestamp
    )
}
