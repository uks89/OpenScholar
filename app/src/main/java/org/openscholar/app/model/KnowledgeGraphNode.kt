package org.openscholar.app.model

data class KnowledgeGraphNode(
    val id: String,
    val type: NodeType,
    val label: String,
    val description: String? = null,
    val properties: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class NodeType {
    PAPER,
    CONCEPT,
    AUTHOR,
    TOPIC,
    NOTE,
    PROJECT
}
