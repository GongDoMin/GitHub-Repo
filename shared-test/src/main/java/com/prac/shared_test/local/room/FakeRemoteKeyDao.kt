package com.prac.shared_test.local.room

import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.entity.RemoteKey

class FakeRemoteKeyDao : RemoteKeyDao {

    private val remoteKeys = mutableListOf<RemoteKey>()

    override suspend fun remoteKey(repoId: Int): RemoteKey? {
        return remoteKeys.find { it.repoId == repoId }
    }

    override suspend fun insertRemoteKeys(remoteKeys: List<RemoteKey>) {
        this.remoteKeys.addAll(remoteKeys)
    }

    override suspend fun clearRemoteKeys() {
        remoteKeys.clear()
    }
}