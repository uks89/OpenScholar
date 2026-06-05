package org.openscholar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.openscholar.app.data.local.entity.KnowledgeGraphEdgeEntity
import org.openscholar.app.data.local.entity.KnowledgeGraphNodeEntity

@Dao
interface KnowledgeGraphDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: KnowledgeGraphNodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEdge(edge: KnowledgeGraphEdgeEntity)

    @Delete
    suspend fun deleteNode(node: KnowledgeGraphNodeEntity)

    @Delete
    suspend fun deleteEdge(edge: KnowledgeGraphEdgeEntity)

    @Query("SELECT * FROM knowledge_graph_nodes ORDER BY created_at DESC")
    fun getAllNodes(): Flow<List<KnowledgeGraphNodeEntity>>

    @Query("SELECT * FROM knowledge_graph_edges ORDER BY weight DESC")
    fun getAllEdges(): Flow<List<KnowledgeGraphEdgeEntity>>

    @Query("SELECT * FROM knowledge_graph_nodes WHERE id = :id")
    suspend fun getNodeById(id: String): KnowledgeGraphNodeEntity?

    @Query("SELECT * FROM knowledge_graph_edges WHERE id = :id")
    suspend fun getEdgeById(id: String): KnowledgeGraphEdgeEntity?

    @Query("SELECT * FROM knowledge_graph_edges WHERE source_id = :nodeId OR target_id = :nodeId")
    suspend fun getEdgesForNode(nodeId: String): List<KnowledgeGraphEdgeEntity>

    @Query("""
        SELECT * FROM knowledge_graph_nodes WHERE id IN (
            SELECT source_id FROM knowledge_graph_edges WHERE target_id = :nodeId
            UNION
            SELECT target_id FROM knowledge_graph_edges WHERE source_id = :nodeId
        )
    """)
    suspend fun getNeighborNodes(nodeId: String): List<KnowledgeGraphNodeEntity>

    @Query("SELECT * FROM knowledge_graph_nodes WHERE label LIKE '%' || :query || '%' OR type = :query")
    fun searchNodes(query: String): Flow<List<KnowledgeGraphNodeEntity>>
}
