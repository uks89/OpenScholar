package org.openscholar.app.domain.usecase

import org.openscholar.app.data.remote.ai.base.AIProviderManager
import org.openscholar.app.data.remote.ai.base.ChatMessage
import org.openscholar.app.data.remote.ai.base.GenerationOptions
import org.openscholar.app.domain.repository.DocumentRepository
import org.openscholar.app.model.Citation
import javax.inject.Inject

class ChatWithDocumentsUseCase @Inject constructor(
    private val aiProviderManager: AIProviderManager,
    private val documentRepository: DocumentRepository
) {
    suspend operator fun invoke(
        question: String,
        contextDocumentIds: List<String>? = null,
        chatHistory: List<ChatMessage> = emptyList()
    ): ChatResult {
        val contextDocs = if (contextDocumentIds != null) {
            contextDocumentIds.mapNotNull { documentRepository.getDocumentById(it) }
        } else {
            documentRepository.getAllDocuments().let { flow ->
                // Take first 5 documents for context
                var docs = emptyList<org.openscholar.app.model.Document>()
                flow.collect { docs = it.take(5) }
                docs
            }
        }

        val contextText = contextDocs.joinToString("\n\n---\n\n") { doc ->
            "[${doc.title}] ${doc.content.take(2000)}"
        }

        val prompt = buildString {
            appendLine("Answer the question based on the provided documents.")
            appendLine()
            appendLine("Documents:")
            appendLine(contextText)
            appendLine()
            appendLine("Question: $question")
            appendLine()
            appendLine("Provide a detailed answer with citations to the source documents.")
        }

        val messages = chatHistory + ChatMessage("user", prompt)

        return when (val result = aiProviderManager.chat(
            messages = messages,
            options = GenerationOptions(temperature = 0.3f, maxTokens = 2048)
        )) {
            is org.openscholar.app.data.remote.ai.base.AIResult.Success -> {
                ChatResult(
                    answer = result.text,
                    citations = result.citations,
                    modelUsed = result.modelUsed
                )
            }
            is org.openscholar.app.data.remote.ai.base.AIResult.Error -> {
                ChatResult(
                    answer = "I'm sorry, I couldn't process your request: ${result.message}",
                    citations = emptyList()
                )
            }
        }
    }
}

data class ChatResult(
    val answer: String,
    val citations: List<Citation>,
    val modelUsed: String? = null
)
