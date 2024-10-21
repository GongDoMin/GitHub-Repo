package com.prac.local.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.prac.local.room.database.RepositoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {
    @Provides
    fun provideRepositoryDatabase(@ApplicationContext context: Context) : RepositoryDatabase {
        return Room.databaseBuilder(
            context,
            RepositoryDatabase::class.java,
            "Repository.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE repository ADD COLUMN defaultBranch TEXT NOT NULL DEFAULT ''")
        }

    }
}