package org.openscholar.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openscholar.app.data.local.converter.Converters
import org.openscholar.app.data.local.dao.ChatDao
import org.openscholar.app.data.local.dao.DocumentDao
import org.openscholar.app.data.local.dao.DocumentEmbeddingDao
import org.openscholar.app.data.local.dao.KnowledgeGraphDao
import org.openscholar.app.data.local.dao.MemoryDao
import org.openscholar.app.data.local.entity.ChatMessageEntity
import org.openscholar.app.data.local.entity.ChatSessionEntity
import org.openscholar.app.data.local.entity.DocumentEmbeddingEntity
import org.openscholar.app.data.local.entity.DocumentEntity
import org.openscholar.app.data.local.entity.KnowledgeGraphEdgeEntity
import org.openscholar.app.data.local.entity.KnowledgeGraphNodeEntity
import org.openscholar.app.data.local.entity.UserMemoryEntity

@Database(
    entities = [
        DocumentEntity::class,
        DocumentEmbeddingEntity::class,
        KnowledgeGraphNodeEntity::class,
        KnowledgeGraphEdgeEntity::class,
        UserMemoryEntity::class,
        ChatSessionEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OpenScholarDatabase : RoomDatabase() {

    abstract fun documentDao(): DocumentDao
    abstract fun documentEmbeddingDao(): DocumentEmbeddingDao
    abstract fun knowledgeGraphDao(): KnowledgeGraphDao
    abstract fun memoryDao(): MemoryDao
    abstract fun chatDao(): ChatDao

    companion object {
        const val DATABASE_NAME = "openscholar.db"
    }
}
