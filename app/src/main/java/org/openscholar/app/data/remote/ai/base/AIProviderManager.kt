package org.openscholar.app.data.remote.ai.base

import org.openscholar.app.data.remote.ai.huggingface.HuggingFaceProvider
import org.openscholar.app.data.remote.ai.ollama.OllamaProvider
import org.openscholar.app.data.remote.ai.openrouter.OpenRouterProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIProviderManager @Inject constructor() {

    private val providers = mutableMapOf<String, AIProvider>()

    fun registerProvider(name: String, provider: AIProvider) {
        providers[name] = provider
    }

    fun getProvider(name: String): AIProvider? = providers[name]

    fun getAvailableProviders(): List<AIProvider> = providers.values.filter { it.isAvailable }

    fun getDefaultProvider(): AIProvider? {
        return providers.values.firstOrNull { it.isAvailable }
    }

    fun createOpenRouterProvider(apiKey: String, model: String = "qwen/qwen-2.5-72b-instruct"): OpenRouterProvider {
        return OpenRouterProvider(apiKey, model)
    }

    fun createHuggingFaceProvider(apiKey: String, model: String = "Qwen/Qwen2.5-72B-Instruct"): HuggingFaceProvider {
        return HuggingFaceProvider(apiKey, model)
    }

    fun createOllamaProvider(baseUrl: String = "http://localhost:11434", model: String = "qwen2.5:7b"): OllamaProvider {
        return OllamaProvider(baseUrl, model)
    }

    suspend fun generateText(
        prompt: String,
        systemPrompt: String? = null,
        options: GenerationOptions = GenerationOptions(),
        preferredProvider: String? = null
    ): AIResult {
        val provider = preferredProvider?.let { getProvider(it) } ?: getDefaultProvider()
        return provider?.generateText(prompt, systemPrompt, options)
            ?: AIResult.Error("No AI provider available")
    }

    suspend fun chat(
        messages: List<ChatMessage>,
        options: GenerationOptions = GenerationOptions(),
        preferredProvider: String? = null
    ): AIResult {
        val provider = preferredProvider?.let { getProvider(it) } ?: getDefaultProvider()
        return provider?.chat(messages, options)
            ?: AIResult.Error("No AI provider available")
    }

    suspend fun generateEmbedding(
        text: String,
        preferredProvider: String? = null
    ): Result<List<Float>> {
        val provider = preferredProvider?.let { getProvider(it) } ?: getDefaultProvider()
        return provider?.generateEmbedding(text)
            ?: Result.failure(Exception("No AI provider available"))
    }
}
