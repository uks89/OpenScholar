package org.openscholar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.openscholar.app.model.FileType
import java.util.UUID

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    @ColumnInfo(name = "file_path") val filePath: String? = null,
    @ColumnInfo(name = "file_type") val fileType: FileType = FileType.NOTE,
    @ColumnInfo(name = "mime_type") val mimeType: String = "text/plain",
    val size: Long = 0,
    val authors: String = "",        // JSON array
    val abstract: String = "",
    val keywords: String = "",       // JSON array
    val references: String = "",     // JSON array
    @ColumnInfo(name = "publication_date") val publicationDate: Long? = null,
    val doi: String? = null,
    @ColumnInfo(name = "source_url") val sourceUrl: String? = null,
    val tags: String = "",           // JSON array
    val summary: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
