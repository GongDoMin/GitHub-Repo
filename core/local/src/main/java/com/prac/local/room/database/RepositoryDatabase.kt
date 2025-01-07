package com.prac.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.dao.RepositoryDao
import com.prac.local.model.RemoteKeyEntity
import com.prac.local.model.RepositoryEntity

@Database(
    version = 2,
    entities = [RepositoryEntity::class, RemoteKeyEntity::class]
)
abstract class RepositoryDatabase : RoomDatabase() {

    abstract fun repositoryDao(): RepositoryDao

    abstract fun remoteKeyDao(): RemoteKeyDao
}