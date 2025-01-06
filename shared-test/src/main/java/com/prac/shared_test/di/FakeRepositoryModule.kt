package com.prac.shared_test.di

import com.prac.data.di.RepositoryModule
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.data.repository.UserRepository
import com.prac.local.room.database.RepositoryDatabase
import com.prac.shared_test.data.FakeRepoRepository
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.data.FakeUserRepository
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

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository =
        FakeUserRepository("test")
}