package com.prac.local

import com.prac.local.model.RemoteKeyEntity

interface RemoteKeyLocalDataSource {
    suspend fun remoteKey(repoId: Int): RemoteKeyEntity?

    suspend fun insertRemoteKeys(remoteKeys: List<RemoteKeyEntity>)

    suspend fun clearRemoteKeys()
}