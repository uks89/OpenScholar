package org.openscholar.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.openscholar.app.model.KnowledgeGraphEdge
import org.openscholar.app.model.KnowledgeGraphNode

interface KnowledgeGraphRepository {

    fun getAllNodes(): Flow<List<KnowledgeGraphNode>>

    fun getAllEdges(): Flow<List<KnowledgeGraphEdge>>

    suspend fun addNode(node: KnowledgeGraphNode)

    suspend fun addEdge(edge: KnowledgeGraphEdge)

    suspend fun deleteNode(id: String)

    suspend fun deleteEdge(id: String)

    suspend fun getNeighborNodes(nodeId: String): List<KnowledgeGraphNode>

    fun searchNodes(query: String): Flow<List<KnowledgeGraphNode>>
}
