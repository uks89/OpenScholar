package org.openscholar.app.domain.usecase

import org.openscholar.app.data.remote.ai.base.AIProviderManager
import org.openscholar.app.data.remote.ai.base.GenerationOptions
import org.openscholar.app.model.Document
import javax.inject.Inject

class GenerateDocumentSummaryUseCase @Inject constructor(
    private val aiProviderManager: AIProviderManager
) {
    suspend operator fun invoke(document: Document): Result<String> {
        val prompt = buildString {
            appendLine("Summarize the following document:")
            appendLine()
            appendLine("Title: ${document.title}")
            appendLine("Authors: ${document.metadata.authors.joinToString(", ")}")
            appendLine()
            appendLine("Content:")
            appendLine(document.content.take(10000))
            appendLine()
            appendLine("Provide a concise summary covering key points, main arguments, and conclusions.")
        }

        return when (val result = aiProviderManager.generateText(
            prompt = prompt,
            systemPrompt = "You are an academic research assistant. Provide clear, structured summaries.",
            options = GenerationOptions(temperature = 0.3f, maxTokens = 1024)
        )) {
            is org.openscholar.app.data.remote.ai.base.AIResult.Success -> {
                Result.success(result.text)
            }
            is org.openscholar.app.data.remote.ai.base.AIResult.Error -> {
                Result.failure(result.throwable ?: Exception(result.message))
            }
        }
    }
}
