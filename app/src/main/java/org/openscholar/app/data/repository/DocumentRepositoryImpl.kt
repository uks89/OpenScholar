package org.openscholar.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.openscholar.app.data.local.dao.DocumentDao
import org.openscholar.app.data.local.entity.DocumentEntity
import org.openscholar.app.domain.repository.DocumentRepository
import org.openscholar.app.model.Document
import org.openscholar.app.model.DocumentMetadata
import org.openscholar.app.model.FileType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao
) : DocumentRepository {

    override fun getAllDocuments(): Flow<List<Document>> {
        return documentDao.getAllDocuments().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDocumentById(id: String): Document? {
        return documentDao.getDocumentById(id)?.toDomain()
    }

    override suspend fun saveDocument(document: Document) {
        documentDao.insertDocument(document.toEntity())
    }

    override suspend fun updateDocument(document: Document) {
        documentDao.updateDocument(document.toEntity())
    }

    override suspend fun deleteDocument(id: String) {
        documentDao.deleteDocumentById(id)
    }

    override fun searchDocuments(query: String): Flow<List<Document>> {
        return documentDao.searchDocuments(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDocumentsByType(fileType: FileType): Flow<List<Document>> {
        return documentDao.getDocumentsByType(fileType.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDocumentCount(): Int {
        return documentDao.getDocumentCount()
    }

    private fun DocumentEntity.toDomain(): Document {
        return Document(
            id = id,
            title = title,
            content = content,
            filePath = filePath,
            fileType = fileType,
            mimeType = mimeType,
            size = size,
            metadata = DocumentMetadata(
                authors = parseJsonArray(authors),
                abstract = abstract,
                keywords = parseJsonArray(keywords),
                references = parseJsonArray(references),
                publicationDate = publicationDate,
                doi = doi,
                sourceUrl = sourceUrl
            ),
            tags = parseJsonArray(tags),
            summary = summary,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Document.toEntity(): DocumentEntity {
        return DocumentEntity(
            id = id,
            title = title,
            content = content,
            filePath = filePath,
            fileType = fileType,
            mimeType = mimeType,
            size = size,
            authors = toJsonArray(metadata.authors),
            abstract = metadata.abstract,
            keywords = toJsonArray(metadata.keywords),
            references = toJsonArray(metadata.references),
            publicationDate = metadata.publicationDate,
            doi = metadata.doi,
            sourceUrl = metadata.sourceUrl,
            tags = toJsonArray(tags),
            summary = summary,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun parseJsonArray(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        return try {
            json.removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun toJsonArray(list: List<String>): String {
        return list.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ","
        ) { "\"$it\"" }
    }
}
