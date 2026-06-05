package org.openscholar.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.openscholar.app.model.Memory
import org.openscholar.app.model.MemoryType

interface MemoryRepository {

    suspend fun saveMemory(memory: Memory)

    suspend fun getMemory(key: String, context: String? = null): Memory?

    fun getMemoriesByContext(context: String): Flow<List<Memory>>

    fun getMemoriesByType(memoryType: MemoryType): Flow<List<Memory>>

    fun searchMemories(query: String): Flow<List<Memory>>

    suspend fun deleteMemory(id: String)

    suspend fun clearExpiredMemories()
}
