package org.openscholar.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.openscholar.app.data.local.dao.KnowledgeGraphDao
import org.openscholar.app.data.local.entity.KnowledgeGraphEdgeEntity
import org.openscholar.app.data.local.entity.KnowledgeGraphNodeEntity
import org.openscholar.app.domain.repository.KnowledgeGraphRepository
import org.openscholar.app.model.KnowledgeGraphEdge
import org.openscholar.app.model.KnowledgeGraphNode
import org.openscholar.app.model.NodeType
import org.openscholar.app.model.RelationshipType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnowledgeGraphRepositoryImpl @Inject constructor(
    private val graphDao: KnowledgeGraphDao
) : KnowledgeGraphRepository {

    override fun getAllNodes(): Flow<List<KnowledgeGraphNode>> {
        return graphDao.getAllNodes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllEdges(): Flow<List<KnowledgeGraphEdge>> {
        return graphDao.getAllEdges().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addNode(node: KnowledgeGraphNode) {
        graphDao.insertNode(node.toEntity())
    }

    override suspend fun addEdge(edge: KnowledgeGraphEdge) {
        graphDao.insertEdge(edge.toEntity())
    }

    override suspend fun deleteNode(id: String) {
        graphDao.getNodeById(id)?.let { graphDao.deleteNode(it) }
    }

    override suspend fun deleteEdge(id: String) {
        graphDao.getEdgeById(id)?.let { graphDao.deleteEdge(it) }
    }

    override suspend fun getNeighborNodes(nodeId: String): List<KnowledgeGraphNode> {
        return graphDao.getNeighborNodes(nodeId).map { it.toDomain() }
    }

    override fun searchNodes(query: String): Flow<List<KnowledgeGraphNode>> {
        return graphDao.searchNodes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun KnowledgeGraphNodeEntity.toDomain(): KnowledgeGraphNode {
        return KnowledgeGraphNode(
            id = id,
            type = type,
            label = label,
            description = description,
            properties = parseProperties(properties),
            createdAt = createdAt
        )
    }

    private fun KnowledgeGraphEdgeEntity.toDomain(): KnowledgeGraphEdge {
        return KnowledgeGraphEdge(
            id = id,
            sourceId = sourceId,
            targetId = targetId,
            relationshipType = relationshipType,
            weight = weight,
            properties = parseProperties(properties),
            createdAt = createdAt
        )
    }

    private fun KnowledgeGraphNode.toEntity(): KnowledgeGraphNodeEntity {
        return KnowledgeGraphNodeEntity(
            id = id,
            type = type,
            label = label,
            description = description,
            properties = properties.entries.joinToString(",") { "${it.key}:${it.value}" },
            createdAt = createdAt
        )
    }

    private fun KnowledgeGraphEdge.toEntity(): KnowledgeGraphEdgeEntity {
        return KnowledgeGraphEdgeEntity(
            id = id,
            sourceId = sourceId,
            targetId = targetId,
            relationshipType = relationshipType,
            weight = weight,
            properties = properties.entries.joinToString(",") { "${it.key}:${it.value}" },
            createdAt = createdAt
        )
    }

    private fun parseProperties(props: String): Map<String, String> {
        if (props.isBlank() || props == "{}") return emptyMap()
        return try {
            props.split(",").associate { entry ->
                val parts = entry.split(":", limit = 2)
                parts[0].trim() to parts.getOrElse(1) { "" }.trim()
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
