package org.openscholar.app.data.remote.ai.base

import org.openscholar.app.model.Citation

interface AIProvider {

    suspend fun generateText(
        prompt: String,
        systemPrompt: String? = null,
        options: GenerationOptions = GenerationOptions()
    ): AIResult

    suspend fun generateEmbedding(text: String): Result<List<Float>>

    suspend fun generateEmbeddings(texts: List<String>): Result<List<List<Float>>>

    suspend fun chat(
        messages: List<ChatMessage>,
        options: GenerationOptions = GenerationOptions()
    ): AIResult

    val providerName: String
    val isAvailable: Boolean
}

data class GenerationOptions(
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048,
    val topP: Float = 0.9f,
    val topK: Int = 40,
    val repetitionPenalty: Float = 1.0f,
    val stopSequences: List<String> = emptyList(),
    val model: String? = null
)

data class ChatMessage(
    val role: String,
    val content: String
)

sealed class AIResult {
    data class Success(
        val text: String,
        val citations: List<Citation> = emptyList(),
        val modelUsed: String? = null,
        val usage: TokenUsage? = null
    ) : AIResult()

    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : AIResult()
}

data class TokenUsage(
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val totalTokens: Int = 0
)
