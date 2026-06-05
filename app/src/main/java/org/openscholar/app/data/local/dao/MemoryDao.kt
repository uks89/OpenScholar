package org.openscholar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.openscholar.app.data.local.entity.UserMemoryEntity

@Dao
interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: UserMemoryEntity)

    @Query("SELECT * FROM user_memory WHERE `key` = :key AND context = :context LIMIT 1")
    suspend fun getMemory(key: String, context: String?): UserMemoryEntity?

    @Query("SELECT * FROM user_memory WHERE context = :context ORDER BY updated_at DESC")
    fun getMemoriesByContext(context: String): Flow<List<UserMemoryEntity>>

    @Query("SELECT * FROM user_memory WHERE memory_type = :memoryType ORDER BY updated_at DESC")
    fun getMemoriesByType(memoryType: String): Flow<List<UserMemoryEntity>>

    @Query("SELECT * FROM user_memory WHERE `key` LIKE '%' || :query || '%' OR value LIKE '%' || :query || '%'")
    fun searchMemories(query: String): Flow<List<UserMemoryEntity>>

    @Query("DELETE FROM user_memory WHERE id = :id")
    suspend fun deleteMemory(id: String)

    @Query("DELETE FROM user_memory WHERE expires_at IS NOT NULL AND expires_at < :now")
    suspend fun deleteExpiredMemories(now: Long = System.currentTimeMillis())
}
