package org.openscholar.app.agent

import org.openscholar.app.data.remote.ai.base.AIProviderManager
import org.openscholar.app.data.remote.ai.base.GenerationOptions
import org.openscholar.app.domain.repository.DocumentRepository
import org.openscholar.app.model.Document
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibrarianAgent @Inject constructor(
    private val aiProviderManager: AIProviderManager,
    private val documentRepository: DocumentRepository
) : Agent<AgentMessage, AgentResponse> {

    override val name: String = "Librarian"
    override val description: String = "Manages documents, extracts metadata, and generates summaries"

    override suspend fun process(request: AgentMessage): AgentResponse {
        return when (request) {
            is AgentMessage.ProcessDocument -> processDocument(request.document)
            is AgentMessage.GenerateSummary -> generateSummary(request.document)
            is AgentMessage.ExtractKeyConcepts -> extractConcepts(request.document)
            else -> AgentResponse.Error("Message type not supported by LibrarianAgent")
        }
    }

    private suspend fun processDocument(document: Document): AgentResponse {
        val prompt = buildString {
            appendLine("Extract metadata from the following document:")
            appendLine()
            appendLine("Title: ${document.title}")
            appendLine("Content: ${document.content.take(5000)}")
            appendLine()
            appendLine("Extract and return:")
            appendLine("- Authors")
            appendLine("- Keywords")
            appendLine("- Abstract (if present)")
            appendLine("- References cited")
        }

        return when (val result = aiProviderManager.generateText(
            prompt = prompt,
            systemPrompt = "You are a research librarian. Extract metadata precisely.",
            options = GenerationOptions(temperature = 0.1f, maxTokens = 1024)
        )) {
            is org.openscholar.app.data.remote.ai.base.AIResult.Success -> {
                val updatedDoc = document.copy(summary = result.text.take(500))
                documentRepository.updateDocument(updatedDoc)
                AgentResponse.TextResponse("Document processed: ${document.title}")
            }
            is org.openscholar.app.data.remote.ai.base.AIResult.Error -> {
                AgentResponse.Error(result.message)
            }
        }
    }

    private suspend fun generateSummary(document: Document): AgentResponse {
        val prompt = buildString {
            appendLine("Summarize the following document concisively:")
            appendLine()
            appendLine("Title: ${document.title}")
            appendLine("Content: ${document.content.take(8000)}")
            appendLine()
            appendLine("Provide a structured summary with:")
            appendLine("- Key findings")
            appendLine("- Main arguments")
            appendLine("- Conclusions")
            appendLine("- Limitations")
        }

        return when (val result = aiProviderManager.generateText(
            prompt = prompt,
            systemPrompt = "You are an academic research assistant. Generate clear, structured summaries.",
            options = GenerationOptions(temperature = 0.3f)
        )) {
            is org.openscholar.app.data.remote.ai.base.AIResult.Success -> {
                AgentResponse.TextResponse(result.text)
            }
            is org.openscholar.app.data.remote.ai.base.AIResult.Error -> {
                AgentResponse.Error(result.message)
            }
        }
    }

    private suspend fun extractConcepts(document: Document): AgentResponse {
        val prompt = buildString {
            appendLine("Extract key concepts and entities from this document:")
            appendLine()
            appendLine(document.content.take(5000))
            appendLine()
            appendLine("Return as a list of concepts with their types (method, theory, dataset, etc.)")
        }

        return when (val result = aiProviderManager.generateText(
            prompt = prompt,
            options = GenerationOptions(temperature = 0.2f)
        )) {
            is org.openscholar.app.data.remote.ai.base.AIResult.Success -> {
                AgentResponse.TextResponse(result.text)
            }
            is org.openscholar.app.data.remote.ai.base.AIResult.Error -> {
                AgentResponse.Error(result.message)
            }
        }
    }

    override suspend fun initialize() = Unit
    override suspend fun shutdown() = Unit
}
