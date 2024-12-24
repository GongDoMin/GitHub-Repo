package com.prac.local.impl

import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.entity.RemoteKey
import javax.inject.Inject

internal class RemoteKeyLocalDataSourceImpl @Inject constructor(
    private val remoteKeyDao: RemoteKeyDao
) : RemoteKeyLocalDataSource {
    override suspend fun remoteKey(repoId: Int): RemoteKey? =
        remoteKeyDao.remoteKey(repoId)

    override suspend fun insertRemoteKeys(remoteKeys: List<RemoteKey>) =
        remoteKeyDao.insertRemoteKeys(remoteKeys)

    override suspend fun clearRemoteKeys() =
        remoteKeyDao.clearRemoteKeys()

}