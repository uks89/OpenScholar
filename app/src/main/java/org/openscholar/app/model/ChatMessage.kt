package org.openscholar.app.model

data class ChatMessage(
    val id: String,
    val sessionId: String,
    val role: MessageRole,
    val content: String,
    val citations: List<Citation> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class Citation(
    val documentId: String,
    val documentTitle: String,
    val excerpt: String,
    val page: Int? = null,
    val relevanceScore: Float = 1.0f
)
