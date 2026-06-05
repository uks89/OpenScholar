package org.openscholar.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.openscholar.app.data.local.dao.MemoryDao
import org.openscholar.app.data.local.entity.UserMemoryEntity
import org.openscholar.app.domain.repository.MemoryRepository
import org.openscholar.app.model.Memory
import org.openscholar.app.model.MemoryType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepositoryImpl @Inject constructor(
    private val memoryDao: MemoryDao
) : MemoryRepository {

    override suspend fun saveMemory(memory: Memory) {
        memoryDao.insertMemory(memory.toEntity())
    }

    override suspend fun getMemory(key: String, context: String?): Memory? {
        return memoryDao.getMemory(key, context)?.toDomain()
    }

    override fun getMemoriesByContext(context: String): Flow<List<Memory>> {
        return memoryDao.getMemoriesByContext(context).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMemoriesByType(memoryType: MemoryType): Flow<List<Memory>> {
        return memoryDao.getMemoriesByType(memoryType.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchMemories(query: String): Flow<List<Memory>> {
        return memoryDao.searchMemories(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteMemory(id: String) {
        memoryDao.deleteMemory(id)
    }

    override suspend fun clearExpiredMemories() {
        memoryDao.deleteExpiredMemories()
    }

    private fun UserMemoryEntity.toDomain(): Memory {
        return Memory(
            id = id,
            memoryType = memoryType,
            key = key,
            value = value,
            context = context,
            expiresAt = expiresAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Memory.toEntity(): UserMemoryEntity {
        return UserMemoryEntity(
            id = id,
            memoryType = memoryType,
            key = key,
            value = value,
            context = context,
            expiresAt = expiresAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
