package org.openscholar.app.model

data class DocumentMetadata(
    val authors: List<String> = emptyList(),
    val abstract: String = "",
    val keywords: List<String> = emptyList(),
    val references: List<String> = emptyList(),
    val publicationDate: Long? = null,
    val doi: String? = null,
    val sourceUrl: String? = null,
    val language: String = "en",
    val pageCount: Int = 0,
    val wordCount: Int = 0
)
