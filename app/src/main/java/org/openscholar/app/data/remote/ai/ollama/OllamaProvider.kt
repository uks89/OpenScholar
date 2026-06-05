package org.openscholar.app.data.remote.ai.ollama

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

class OllamaProvider(
    private val baseUrl: String = "http://localhost:11434",
    private val defaultModel: String = "qwen2.5:7b"
) : AIProvider {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val jsonMediaType = "application/json".toMediaType()

    override val providerName: String = "Ollama"
    override val isAvailable: Boolean = true

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
            val requestBody = OllamaEmbeddingRequest(model = defaultModel, prompt = text)
            val json = moshi.adapter(OllamaEmbeddingRequest::class.java).toJson(requestBody)
            val request = Request.Builder()
                .url("$baseUrl/api/embeddings")
                .post(json.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return Result.failure(Exception("Empty response"))
            val embeddingResponse = moshi.adapter(OllamaEmbeddingResponse::class.java).fromJson(body)
            Result.success(embeddingResponse?.embedding ?: return Result.failure(Exception("No embedding")))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateEmbeddings(texts: List<String>): Result<List<List<Float>>> {
        return try {
            val results = texts.map { text ->
                when (val result = generateEmbedding(text)) {
                    is Result.Success -> result.getOrThrow()
                    is Result.Failure -> return Result.failure(result.exceptionOrNull()!!)
                }
            }
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun chat(
        messages: List<ChatMessage>,
        options: GenerationOptions
    ): AIResult {
        return try {
            val ollamaMessages = messages.map { OllamaMessage(it.role, it.content) }
            val requestBody = OllamaChatRequest(
                model = options.model ?: defaultModel,
                messages = ollamaMessages,
                options = OllamaRequestOptions(
                    temperature = options.temperature,
                    topP = options.topP,
                    topK = options.topK
                ),
                stream = false
            )

            val json = moshi.adapter(OllamaChatRequest::class.java).toJson(requestBody)
            val request = Request.Builder()
                .url("$baseUrl/api/chat")
                .post(json.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return AIResult.Error("Empty response")

            if (!response.isSuccessful) return AIResult.Error("API error: $body")

            val chatResponse = moshi.adapter(OllamaChatResponse::class.java).fromJson(body)
            AIResult.Success(
                text = chatResponse?.message?.content ?: "",
                modelUsed = chatResponse?.model
            )
        } catch (e: Exception) {
            AIResult.Error(e.message ?: "Unknown error", e)
        }
    }
}

@JsonClass(generateAdapter = false)
data class OllamaChatRequest(
    val model: String,
    val messages: List<OllamaMessage>,
    val options: OllamaRequestOptions? = null,
    val stream: Boolean = false
)

@JsonClass(generateAdapter = false)
data class OllamaMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = false)
data class OllamaChatResponse(
    val model: String?,
    val message: OllamaResponseMessage?,
    val done: Boolean?
)

@JsonClass(generateAdapter = false)
data class OllamaResponseMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = false)
data class OllamaRequestOptions(
    val temperature: Double = 0.7,
    @com.squareup.moshi.Json(name = "top_p") val topP: Double = 0.9,
    @com.squareup.moshi.Json(name = "top_k") val topK: Int = 40
)

@JsonClass(generateAdapter = false)
data class OllamaEmbeddingRequest(
    val model: String,
    val prompt: String
)

@JsonClass(generateAdapter = false)
data class OllamaEmbeddingResponse(
    val embedding: List<Float>
)
