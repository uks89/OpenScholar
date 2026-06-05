package org.openscholar.app.model

data class KnowledgeGraphEdge(
    val id: String,
    val sourceId: String,
    val targetId: String,
    val relationshipType: RelationshipType,
    val weight: Float = 1.0f,
    val properties: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class RelationshipType {
    CITES,
    RELATED_TO,
    AUTHORED_BY,
    PART_OF,
    SIMILAR_TO,
    CONTRADICTS,
    BUILD_UPON,
    USES_METHOD
}
