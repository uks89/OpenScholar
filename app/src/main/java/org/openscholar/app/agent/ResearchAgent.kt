package org.openscholar.app.agent

import org.openscholar.app.data.remote.ai.base.AIProviderManager
import org.openscholar.app.data.remote.ai.base.GenerationOptions
import org.openscholar.app.domain.repository.DocumentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResearchAgent @Inject constructor(
    private val aiProviderManager: AIProviderManager,
    private val documentRepository: DocumentRepository
) : Agent<AgentMessage, AgentResponse> {

    override val name: String = "ResearchAnalyst"
    override val description: String = "Analyzes papers, detects gaps, and generates literature reviews"

    override suspend fun process(request: AgentMessage): AgentResponse {
        return when (request) {
            is AgentMessage.AnalyzePapers -> analyzePapers(request.documentIds)
            is AgentMessage.GenerateLiteratureReview -> generateLiteratureReview(request.documentIds)
            is AgentMessage.DetectResearchGaps -> detectResearchGaps(request.documentIds)
            else -> AgentResponse.Error("Message type not supported by ResearchAgent")
        }
    }

    private suspend fun analyzePapers(documentIds: List<String>): AgentResponse {
        val documents = documentIds.mapNotNull { documentRepository.getDocumentById(it) }
        if (documents.isEmpty()) return AgentResponse.Error("No documents found")

        val context = documents.joinToString("\n\n---\n\n") { doc ->
            buildString {
                appendLine("Title: ${doc.title}")
                appendLine("Authors: ${doc.metadata.authors.joinToString(", ")}")
                appendLine("Abstract: ${doc.metadata.abstract}")
                appendLine("Content: ${doc.content.take(2000)}")
            }
        }

        val prompt = buildString {
            appendLine("Analyze the following research papers:")
            appendLine()
            appendLine(context)
            appendLine()
            appendLine("Provide analysis including:")
            appendLine("- Key contributions of each paper")
            appendLine("- Methodologies used")
            appendLine("- Common themes")
            appendLine("- Contradictions or disagreements")
        }

        return when (val result = aiProviderManager.generateText(
            prompt = prompt,
            systemPrompt = "You are a senior research analyst. Provide critical, insightful analysis.",
            options = GenerationOptions(temperature = 0.4f, maxTokens = 2048)
        )) {
            is org.openscholar.app.data.remote.ai.base.AIResult.Success -> {
                AgentResponse.TextResponse(result.text)
            }
            is org.openscholar.app.data.remote.ai.base.AIResult.Error -> {
                AgentResponse.Error(result.message)
            }
        }
    }

    private suspend fun generateLiteratureReview(documentIds: List<String>): AgentResponse {
        val documents = documentIds.mapNotNull { documentRepository.getDocumentById(it) }
        if (documents.isEmpty()) return AgentResponse.Error("No documents found")

        val context = documents.joinToString("\n\n---\n\n") { doc ->
            buildString {
                appendLine("Title: ${doc.title}")
                appendLine("Authors: ${doc.metadata.authors.joinToString(", ")}")
                appendLine("Abstract: ${doc.metadata.abstract}")
                appendLine("Content: ${doc.content.take(2000)}")
            }
        }

        val prompt = buildString {
            appendLine("Generate a literature review based on these papers:")
            appendLine()
            appendLine(context)
            appendLine()
            appendLine("Structure the review with:")
            appendLine("1. Introduction")
            appendLine("2. Related Work and Background")
            appendLine("3. Comparative Analysis")
            appendLine("4. Research Gaps")
            appendLine("5. Future Directions")
            appendLine("6. References")
        }

        return when (val result = aiProviderManager.generateText(
            prompt = prompt,
            systemPrompt = "You are an academic writing assistant. Generate well-structured literature reviews with proper citations.",
            options = GenerationOptions(temperature = 0.5f, maxTokens = 4096)
        )) {
            is org.openscholar.app.data.remote.ai.base.AIResult.Success -> {
                AgentResponse.TextResponse(result.text)
            }
            is org.openscholar.app.data.remote.ai.base.AIResult.Error -> {
                AgentResponse.Error(result.message)
            }
        }
    }

    private suspend fun detectResearchGaps(documentIds: List<String>): AgentResponse {
        val documents = documentIds.mapNotNull { documentRepository.getDocumentById(it) }
        if (documents.isEmpty()) return AgentResponse.Error("No documents found")

        val context = documents.joinToString("\n\n---\n\n") { doc ->
            buildString {
                appendLine("Title: ${doc.title}")
                appendLine("Abstract: ${doc.metadata.abstract}")
                appendLine("Content: ${doc.content.take(2000)}")
            }
        }

        val prompt = buildString {
            appendLine("Analyze this research corpus for gaps:")
            appendLine()
            appendLine(context)
            appendLine()
            appendLine("Identify:")
            appendLine("- Underexplored topics")
            appendLine("- Missing connections between papers")
            appendLine("- Methodological limitations")
            appendLine("- Contradictory findings")
            appendLine("- Emerging trends")
            appendLine("- Novel research directions")
        }

        return when (val result = aiProviderManager.generateText(
            prompt = prompt,
            systemPrompt = "You are a research strategist. Identify meaningful research gaps and opportunities.",
            options = GenerationOptions(temperature = 0.6f, maxTokens = 2048)
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
