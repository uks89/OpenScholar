package org.openscholar.app.model

data class Memory(
    val id: String,
    val memoryType: MemoryType,
    val key: String,
    val value: String,
    val context: String? = null,
    val expiresAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class MemoryType {
    SHORT_TERM,
    LONG_TERM,
    PROJECT_SPECIFIC
}
