package com.prac.local

import com.prac.local.room.entity.RemoteKey

interface RemoteKeyLocalDataSource {
    suspend fun remoteKey(repoId: Int): RemoteKey?

    suspend fun insertRemoteKeys(remoteKeys: List<RemoteKey>)

    suspend fun clearRemoteKeys()
}