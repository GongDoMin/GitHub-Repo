package com.prac.data.fake.di

import com.prac.data.fake.repository.FakeRepoRepository
import com.prac.data.fake.repository.FakeTokenRepository
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.data.repository.di.RepositoryModule
import com.prac.data.repository.impl.RepoRepositoryImpl
import com.prac.data.source.local.room.database.RepositoryDatabase
import com.prac.data.source.network.RepoApiDataSource
import com.prac.data.source.network.RepoStarApiDataSource
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