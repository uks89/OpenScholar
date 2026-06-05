package org.openscholar.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.openscholar.app.data.repository.DocumentRepositoryImpl
import org.openscholar.app.data.repository.KnowledgeGraphRepositoryImpl
import org.openscholar.app.domain.repository.DocumentRepository
import org.openscholar.app.domain.repository.KnowledgeGraphRepository
import org.openscholar.app.domain.repository.MemoryRepository
import org.openscholar.app.data.repository.MemoryRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDocumentRepository(impl: DocumentRepositoryImpl): DocumentRepository

    @Binds
    @Singleton
    abstract fun bindKnowledgeGraphRepository(impl: KnowledgeGraphRepositoryImpl): KnowledgeGraphRepository

    @Binds
    @Singleton
    abstract fun bindMemoryRepository(impl: MemoryRepositoryImpl): MemoryRepository
}
