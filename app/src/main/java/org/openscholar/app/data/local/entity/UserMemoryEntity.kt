package org.openscholar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.openscholar.app.model.MemoryType
import java.util.UUID

@Entity(
    tableName = "user_memory",
    indices = [Index("key", "context")]
)
data class UserMemoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "memory_type") val memoryType: MemoryType,
    val key: String,
    val value: String,
    val context: String? = null,
    @ColumnInfo(name = "expires_at") val expiresAt: Long? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
