package com.prac.shared_test.local.room

import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.model.RemoteKeyEntity

class FakeRemoteKeyDao : RemoteKeyDao {

    private val remoteKeys = mutableListOf<RemoteKeyEntity>()

    override suspend fun remoteKey(repoId: Int): RemoteKeyEntity? {
        return remoteKeys.find { it.repoId == repoId }
    }

    override suspend fun insertRemoteKeys(remoteKeys: List<RemoteKeyEntity>) {
        this.remoteKeys.addAll(remoteKeys)
    }

    override suspend fun clearRemoteKeys() {
        remoteKeys.clear()
    }
}