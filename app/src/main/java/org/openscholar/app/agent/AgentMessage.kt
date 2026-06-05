package org.openscholar.app.agent

import org.openscholar.app.model.Document

sealed class AgentMessage {
    data class ProcessDocument(val document: Document) : AgentMessage()
    data class GenerateSummary(val document: Document) : AgentMessage()
    data class AnswerQuestion(val question: String, val contextIds: List<String>? = null) : AgentMessage()
    data class AnalyzePapers(val documentIds: List<String>) : AgentMessage()
    data class GenerateLiteratureReview(val documentIds: List<String>) : AgentMessage()
    data class DetectResearchGaps(val documentIds: List<String>) : AgentMessage()
    data class CreateLearningPlan(val topic: String, val skillLevel: String = "beginner") : AgentMessage()
    data class ExtractKeyConcepts(val document: Document) : AgentMessage()
}

sealed class AgentResponse {
    data class TextResponse(val text: String, val citations: List<String> = emptyList()) : AgentResponse()
    data class DocumentResponse(val documents: List<Document>) : AgentResponse()
    data class Error(val message: String) : AgentResponse()
}
