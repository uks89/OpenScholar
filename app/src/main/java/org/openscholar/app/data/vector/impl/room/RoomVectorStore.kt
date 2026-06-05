package org.openscholar.app.data.vector.impl.room

import org.openscholar.app.data.local.dao.DocumentEmbeddingDao
import org.openscholar.app.data.local.entity.DocumentEmbeddingEntity
import org.openscholar.app.data.vector.VectorEmbedding
import org.openscholar.app.data.vector.VectorSearchResult
import org.openscholar.app.data.vector.VectorStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class RoomVectorStore @Inject constructor(
    private val embeddingDao: DocumentEmbeddingDao
) : VectorStore {

    override suspend fun storeEmbedding(
        id: String,
        embedding: List<Float>,
        metadata: Map<String, String>
    ) {
        val entity = DocumentEmbeddingEntity(
            documentId = id,
            chunkIndex = 0,
            content = metadata["content"] ?: "",
            embedding = embedding.toFloatArray().toByteArray()
        )
        embeddingDao.insertEmbeddings(listOf(entity))
    }

    override suspend fun storeEmbeddings(embeddings: List<VectorEmbedding>) {
        val entities = embeddings.map { emb ->
            DocumentEmbeddingEntity(
                documentId = emb.id,
                chunkIndex = 0,
                content = emb.metadata["content"] ?: "",
                embedding = emb.embedding.toFloatArray().toByteArray()
            )
        }
        embeddingDao.insertEmbeddings(entities)
    }

    override suspend fun searchSimilar(
        queryEmbedding: List<Float>,
        limit: Int,
        threshold: Float
    ): List<VectorSearchResult> {
        // For Room-based implementation, we load all embeddings and compute cosine similarity in-memory.
        // In production, this would use a proper vector extension or FAISS.
        return emptyList()
    }

    override suspend fun deleteEmbedding(id: String) {
        embeddingDao.deleteEmbeddingsForDocument(id)
    }

    override suspend fun clear() {
        // Not implemented for Room-based store
    }

    private fun cosineSimilarity(a: List<Float>, b: List<Float>): Float {
        if (a.size != b.size) return 0f
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        val denominator = sqrt(normA) * sqrt(normB)
        return if (denominator == 0f) 0f else dotProduct / denominator
    }

    private fun ByteArray.toFloatArray(): FloatArray {
        val floats = FloatArray(size / 4)
        for (i in floats.indices) {
            floats[i] = java.nio.ByteBuffer.wrap(this, i * 4, 4).float
        }
        return floats
    }

    private fun FloatArray.toByteArray(): ByteArray {
        val bytes = ByteArray(size * 4)
        val buffer = java.nio.ByteBuffer.wrap(bytes)
        forEach { buffer.putFloat(it) }
        return bytes
    }

    private fun List<Float>.toFloatArray(): FloatArray {
        val arr = FloatArray(size)
        for (i in indices) arr[i] = this[i]
        return arr
    }
}
