package com.prac.local.di

import android.content.Context
import androidx.room.Room
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.dao.RepositoryDao
import com.prac.local.room.database.RepositoryDatabase
import com.prac.local.room.migration.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {
    @Provides
    @Singleton
    fun provideRepositoryDatabase(@ApplicationContext context: Context) : RepositoryDatabase {
        return Room.databaseBuilder(
            context,
            RepositoryDatabase::class.java,
            "Repository.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideRepositoryDao(database: RepositoryDatabase) : RepositoryDao {
        return database.repositoryDao()
    }

    @Provides
    fun provideRemoteKeyDao(database: RepositoryDatabase) : RemoteKeyDao {
        return database.remoteKeyDao()
    }
}