package com.prac.data.fake.di

import android.content.Context
import androidx.room.Room
import com.prac.data.source.local.di.DatabaseModule
import com.prac.data.source.local.room.database.RepositoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
internal class FakeDatabaseModule {
    @Provides
    fun provideRepositoryDatabase(@ApplicationContext context: Context) : RepositoryDatabase {
        return Room.inMemoryDatabaseBuilder(context, RepositoryDatabase::class.java)
            .build()
    }
}