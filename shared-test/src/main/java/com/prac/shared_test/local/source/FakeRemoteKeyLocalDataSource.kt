package com.prac.shared_test.local.source

import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.model.RemoteKeyEntity

class FakeRemoteKeyLocalDataSource : RemoteKeyLocalDataSource {

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