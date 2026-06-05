package org.openscholar.app.model

data class Document(
    val id: String,
    val title: String,
    val content: String,
    val filePath: String? = null,
    val fileType: FileType = FileType.NOTE,
    val mimeType: String = "text/plain",
    val size: Long = 0,
    val metadata: DocumentMetadata = DocumentMetadata(),
    val tags: List<String> = emptyList(),
    val summary: String? = null,
    val embedding: FloatArray? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
