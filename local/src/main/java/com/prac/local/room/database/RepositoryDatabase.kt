package com.prac.local.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.dao.RepositoryDao
import com.prac.local.room.entity.RemoteKey
import com.prac.local.room.entity.Repository

@Database(
    entities = [Repository::class, RemoteKey::class],
    version = 1
)
abstract class RepositoryDatabase : RoomDatabase() {

    abstract fun repositoryDao(): RepositoryDao

    abstract fun remoteKeyDao(): RemoteKeyDao
}