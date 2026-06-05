package org.openscholar.app.agent

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentOrchestrator @Inject constructor(
    private val librarianAgent: LibrarianAgent,
    private val researchAgent: ResearchAgent
) {

    suspend fun process(message: AgentMessage): AgentResponse {
        val agent = resolveAgent(message)
        return agent.process(message)
    }

    private fun resolveAgent(message: AgentMessage): Agent<AgentMessage, AgentResponse> {
        return when (message) {
            is AgentMessage.ProcessDocument -> librarianAgent
            is AgentMessage.GenerateSummary -> librarianAgent
            is AgentMessage.ExtractKeyConcepts -> librarianAgent
            is AgentMessage.AnalyzePapers -> researchAgent
            is AgentMessage.GenerateLiteratureReview -> researchAgent
            is AgentMessage.DetectResearchGaps -> researchAgent
            is AgentMessage.AnswerQuestion -> librarianAgent
            is AgentMessage.CreateLearningPlan -> researchAgent
        }
    }

    suspend fun analyzeDocument(document: org.openscholar.app.model.Document): AgentResponse {
        return process(AgentMessage.ProcessDocument(document))
    }

    suspend fun generateSummary(document: org.openscholar.app.model.Document): AgentResponse {
        return process(AgentMessage.GenerateSummary(document))
    }

    suspend fun generateLiteratureReview(documentIds: List<String>): AgentResponse {
        return process(AgentMessage.GenerateLiteratureReview(documentIds))
    }

    suspend fun detectResearchGaps(documentIds: List<String>): AgentResponse {
        return process(AgentMessage.DetectResearchGaps(documentIds))
    }
}
