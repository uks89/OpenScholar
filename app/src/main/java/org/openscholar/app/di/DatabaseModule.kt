package org.openscholar.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.openscholar.app.data.local.dao.ChatDao
import org.openscholar.app.data.local.dao.DocumentDao
import org.openscholar.app.data.local.dao.DocumentEmbeddingDao
import org.openscholar.app.data.local.dao.KnowledgeGraphDao
import org.openscholar.app.data.local.dao.MemoryDao
import org.openscholar.app.data.local.db.OpenScholarDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OpenScholarDatabase {
        return Room.databaseBuilder(
            context,
            OpenScholarDatabase::class.java,
            OpenScholarDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDocumentDao(database: OpenScholarDatabase): DocumentDao = database.documentDao()

    @Provides
    fun provideDocumentEmbeddingDao(database: OpenScholarDatabase): DocumentEmbeddingDao =
        database.documentEmbeddingDao()

    @Provides
    fun provideKnowledgeGraphDao(database: OpenScholarDatabase): KnowledgeGraphDao =
        database.knowledgeGraphDao()

    @Provides
    fun provideMemoryDao(database: OpenScholarDatabase): MemoryDao = database.memoryDao()

    @Provides
    fun provideChatDao(database: OpenScholarDatabase): ChatDao = database.chatDao()
}
