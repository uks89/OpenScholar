package org.openscholar.app.data.remote.ai.openrouter

import com.squareup.moshi.Moshi
import com.squareup.moshi.JsonClass
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.openscholar.app.data.remote.ai.base.AIProvider
import org.openscholar.app.data.remote.ai.base.AIResult
import org.openscholar.app.data.remote.ai.base.ChatMessage
import org.openscholar.app.data.remote.ai.base.GenerationOptions
import org.openscholar.app.data.remote.ai.base.TokenUsage
import org.openscholar.app.model.Citation
import java.util.concurrent.TimeUnit

class OpenRouterProvider(
    private val apiKey: String,
    private val defaultModel: String = "qwen/qwen-2.5-72b-instruct"
) : AIProvider {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val jsonMediaType = "application/json".toMediaType()

    override val providerName: String = "OpenRouter"

    override val isAvailable: Boolean = apiKey.isNotBlank()

    override suspend fun generateText(
        prompt: String,
        systemPrompt: String?,
        options: GenerationOptions
    ): AIResult {
        val messages = buildList {
            systemPrompt?.let { add(ChatMessage("system", it)) }
            add(ChatMessage("user", prompt))
        }
        return chat(messages, options)
    }

    override suspend fun generateEmbedding(text: String): Result<List<Float>> {
        return try {
            val requestBody = OpenRouterEmbeddingRequest(
                model = "text-embedding-ada-002",
                input = text
            )
            val json = moshi.adapter(OpenRouterEmbeddingRequest::class.java).toJson(requestBody)
            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/embeddings")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(json.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return Result.failure(Exception("Empty response"))

            if (!response.isSuccessful) {
                return Result.failure(Exception("API error: $body"))
            }

            val embeddingResponse = moshi.adapter(OpenRouterEmbeddingResponse::class.java).fromJson(body)
            val embedding = embeddingResponse?.data?.firstOrNull()?.embedding
                ?: return Result.failure(Exception("No embedding in response"))

            Result.success(embedding)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateEmbeddings(texts: List<String>): Result<List<List<Float>>> {
        val results = mutableListOf<List<Float>>()
        for (text in texts) {
            val result = generateEmbedding(text)
            if (result.isFailure) {
                return Result.failure(result.exceptionOrNull() ?: Exception("Embedding failed"))
            }
            results.add(result.getOrThrow())
        }
        return Result.success(results)
    }

    override suspend fun chat(
        messages: List<ChatMessage>,
        options: GenerationOptions
    ): AIResult {
        return try {
            val chatRequest = OpenRouterChatRequest(
                model = options.model ?: defaultModel,
                messages = messages.map { OpenRouterMessage(it.role, it.content) },
                temperature = options.temperature,
                maxTokens = options.maxTokens,
                topP = options.topP
            )

            val json = moshi.adapter(OpenRouterChatRequest::class.java).toJson(chatRequest)
            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("HTTP-Referer", "https://openscholar.app")
                .addHeader("X-Title", "OpenScholar")
                .post(json.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return AIResult.Error("Empty response")

            if (!response.isSuccessful) {
                return AIResult.Error("API error: $body")
            }

            val chatResponse = moshi.adapter(OpenRouterChatResponse::class.java).fromJson(body)

            if (chatResponse == null) {
                return AIResult.Error("Failed to parse response")
            }

            val text = chatResponse.choices?.firstOrNull()?.message?.content ?: ""
            val usage = chatResponse.usage?.let {
                TokenUsage(
                    promptTokens = it.promptTokens ?: 0,
                    completionTokens = it.completionTokens ?: 0,
                    totalTokens = it.totalTokens ?: 0
                )
            }

            AIResult.Success(
                text = text,
                modelUsed = chatResponse.model,
                usage = usage
            )
        } catch (e: Exception) {
            AIResult.Error(e.message ?: "Unknown error", e)
        }
    }
}

@JsonClass(generateAdapter = false)
data class OpenRouterChatRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val temperature: Float = 0.7f,
    @com.squareup.moshi.Json(name = "max_tokens") val maxTokens: Int = 2048,
    @com.squareup.moshi.Json(name = "top_p") val topP: Float = 0.9f
)

@JsonClass(generateAdapter = false)
data class OpenRouterMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = false)
data class OpenRouterChatResponse(
    val id: String?,
    val model: String?,
    val choices: List<OpenRouterChoice>?,
    val usage: OpenRouterUsage?,
    val error: OpenRouterError?
)

@JsonClass(generateAdapter = false)
data class OpenRouterChoice(
    val index: Int,
    val message: OpenRouterResponseMessage,
    @com.squareup.moshi.Json(name = "finish_reason") val finishReason: String?
)

@JsonClass(generateAdapter = false)
data class OpenRouterResponseMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = false)
data class OpenRouterUsage(
    @com.squareup.moshi.Json(name = "prompt_tokens") val promptTokens: Int?,
    @com.squareup.moshi.Json(name = "completion_tokens") val completionTokens: Int?,
    @com.squareup.moshi.Json(name = "total_tokens") val totalTokens: Int?
)

@JsonClass(generateAdapter = false)
data class OpenRouterError(
    val code: Int?,
    val message: String?
)

@JsonClass(generateAdapter = false)
data class OpenRouterEmbeddingRequest(
    val model: String,
    val input: String
)

@JsonClass(generateAdapter = false)
data class OpenRouterEmbeddingResponse(
    val data: List<OpenRouterEmbeddingData>?,
    val model: String?,
    val usage: OpenRouterUsage?
)

@JsonClass(generateAdapter = false)
data class OpenRouterEmbeddingData(
    val embedding: List<Float>,
    val index: Int
)
