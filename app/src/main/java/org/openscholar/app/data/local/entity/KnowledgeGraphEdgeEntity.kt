package org.openscholar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.openscholar.app.model.RelationshipType
import java.util.UUID

@Entity(
    tableName = "knowledge_graph_edges",
    foreignKeys = [
        ForeignKey(
            entity = KnowledgeGraphNodeEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = KnowledgeGraphNodeEntity::class,
            parentColumns = ["id"],
            childColumns = ["target_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("source_id"), Index("target_id")]
)
data class KnowledgeGraphEdgeEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "source_id") val sourceId: String,
    @ColumnInfo(name = "target_id") val targetId: String,
    @ColumnInfo(name = "relationship_type") val relationshipType: RelationshipType,
    val weight: Float = 1.0f,
    val properties: String = "{}",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
