package org.openscholar.app.data.remote.ai.huggingface

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

class HuggingFaceProvider(
    private val apiKey: String,
    private val defaultModel: String = "Qwen/Qwen2.5-72B-Instruct"
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
    private val baseUrl = "https://api-inference.huggingface.co/models"

    override val providerName: String = "HuggingFace"
    override val isAvailable: Boolean = apiKey.isNotBlank()

    override suspend fun generateText(
        prompt: String,
        systemPrompt: String?,
        options: GenerationOptions
    ): AIResult {
        val fullPrompt = if (systemPrompt != null) "$systemPrompt\n\n$prompt" else prompt
        val requestBody = HFTextGenerationRequest(
            inputs = fullPrompt,
            parameters = HFParameters(
                temperature = options.temperature,
                maxNewTokens = options.maxTokens,
                topP = options.topP
            )
        )

        return try {
            val json = moshi.adapter(HFTextGenerationRequest::class.java).toJson(requestBody)
            val model = options.model ?: defaultModel
            val request = Request.Builder()
                .url("$baseUrl/$model")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(json.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return AIResult.Error("Empty response")

            if (!response.isSuccessful) return AIResult.Error("API error: $body")

            val hfResponse = moshi.adapter(List::class.java).fromJson(body) as? List<Map<String, Any>>
            val text = hfResponse?.firstOrNull()?.get("generated_text") as? String ?: ""
            AIResult.Success(text = text, modelUsed = model)
        } catch (e: Exception) {
            AIResult.Error(e.message ?: "Unknown error", e)
        }
    }

    override suspend fun generateEmbedding(text: String): Result<List<Float>> {
        return try {
            val requestBody = HFEmbeddingRequest(inputs = text)
            val json = moshi.adapter(HFEmbeddingRequest::class.java).toJson(requestBody)
            val request = Request.Builder()
                .url("$baseUrl/intfloat/e5-mistral-7b-instruct")
                .addHeader("Authorization", "Bearer $apiKey")
                .post(json.toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return Result.failure(Exception("Empty response"))
            if (!response.isSuccessful) return Result.failure(Exception("API error: $body"))

            val embedding = moshi.adapter(List::class.java).fromJson(body) as? List<Double>
                ?: return Result.failure(Exception("Failed to parse embedding"))

            Result.success(embedding.map { it.toFloat() })
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
        val formattedPrompt = messages.joinToString("\n") { "${it.role}: ${it.content}" }
        return generateText(formattedPrompt, options = options)
    }
}

@JsonClass(generateAdapter = false)
data class HFTextGenerationRequest(
    val inputs: String,
    val parameters: HFParameters? = null
)

@JsonClass(generateAdapter = false)
data class HFParameters(
    val temperature: Float = 0.7f,
    @com.squareup.moshi.Json(name = "max_new_tokens") val maxNewTokens: Int = 2048,
    @com.squareup.moshi.Json(name = "top_p") val topP: Float = 0.9f,
    @com.squareup.moshi.Json(name = "return_full_text") val returnFullText: Boolean = false
)

@JsonClass(generateAdapter = false)
data class HFEmbeddingRequest(
    val inputs: String
)
