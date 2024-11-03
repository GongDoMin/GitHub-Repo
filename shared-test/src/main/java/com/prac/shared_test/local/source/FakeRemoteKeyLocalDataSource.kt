package com.prac.shared_test.local.source

import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.room.entity.RemoteKey

class FakeRemoteKeyLocalDataSource : RemoteKeyLocalDataSource {

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