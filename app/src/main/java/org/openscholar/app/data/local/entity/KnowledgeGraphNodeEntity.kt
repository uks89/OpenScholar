package org.openscholar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.openscholar.app.model.NodeType
import java.util.UUID

@Entity(tableName = "knowledge_graph_nodes")
data class KnowledgeGraphNodeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: NodeType,
    val label: String,
    val description: String? = null,
    val properties: String = "{}",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
