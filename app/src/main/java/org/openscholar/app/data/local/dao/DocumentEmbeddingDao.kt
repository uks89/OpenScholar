package org.openscholar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.openscholar.app.data.local.entity.DocumentEmbeddingEntity

@Dao
interface DocumentEmbeddingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmbeddings(embeddings: List<DocumentEmbeddingEntity>)

    @Query("SELECT * FROM document_embeddings WHERE document_id = :documentId ORDER BY chunk_index ASC")
    suspend fun getEmbeddingsForDocument(documentId: String): List<DocumentEmbeddingEntity>

    @Query("SELECT * FROM document_embeddings WHERE document_id IN (SELECT id FROM documents WHERE id = :documentId)")
    suspend fun getEmbeddingsByDocumentId(documentId: String): List<DocumentEmbeddingEntity>

    @Query("DELETE FROM document_embeddings WHERE document_id = :documentId")
    suspend fun deleteEmbeddingsForDocument(documentId: String)

    @Query("SELECT COUNT(*) FROM document_embeddings")
    suspend fun getEmbeddingCount(): Int
}
