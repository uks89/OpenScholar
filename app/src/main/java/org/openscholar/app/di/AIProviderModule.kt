package org.openscholar.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.openscholar.app.data.remote.ai.base.AIProviderManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIProviderModule {

    @Provides
    @Singleton
    fun provideAIProviderManager(): AIProviderManager {
        return AIProviderManager()
    }
}
