package org.openscholar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "document_embeddings",
    foreignKeys = [
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["document_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("document_id")]
)
data class DocumentEmbeddingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "document_id") val documentId: String,
    @ColumnInfo(name = "chunk_index") val chunkIndex: Int,
    val content: String,
    val embedding: ByteArray,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
