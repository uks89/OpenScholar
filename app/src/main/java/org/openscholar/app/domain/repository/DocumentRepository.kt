package org.openscholar.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.openscholar.app.model.Document
import org.openscholar.app.model.FileType

interface DocumentRepository {

    fun getAllDocuments(): Flow<List<Document>>

    suspend fun getDocumentById(id: String): Document?

    suspend fun saveDocument(document: Document)

    suspend fun updateDocument(document: Document)

    suspend fun deleteDocument(id: String)

    fun searchDocuments(query: String): Flow<List<Document>>

    fun getDocumentsByType(fileType: FileType): Flow<List<Document>>

    suspend fun getDocumentCount(): Int
}
