package com.prac.data.di

import com.prac.data.fake.FakeRepoRepository
import com.prac.data.fake.FakeTokenRepository
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.local.room.database.RepositoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
internal class FakeRepositoryModule {
    @Provides
    @Singleton
    fun provideTokenRepository(
    ): TokenRepository =
        FakeTokenRepository()

    @Provides
    @Singleton
    fun provideRepoRepository(
        repositoryDatabase: RepositoryDatabase,
    ): RepoRepository =
        FakeRepoRepository(repositoryDatabase)
}