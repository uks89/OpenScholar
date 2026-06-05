package org.openscholar.app.data.vector

interface VectorStore {

    suspend fun storeEmbedding(
        id: String,
        embedding: List<Float>,
        metadata: Map<String, String> = emptyMap()
    )

    suspend fun storeEmbeddings(
        embeddings: List<VectorEmbedding>
    )

    suspend fun searchSimilar(
        queryEmbedding: List<Float>,
        limit: Int = 10,
        threshold: Float = 0.0f
    ): List<VectorSearchResult>

    suspend fun deleteEmbedding(id: String)

    suspend fun clear()
}

data class VectorEmbedding(
    val id: String,
    val embedding: List<Float>,
    val metadata: Map<String, String> = emptyMap()
)

data class VectorSearchResult(
    val id: String,
    val score: Float,
    val metadata: Map<String, String> = emptyMap()
)
