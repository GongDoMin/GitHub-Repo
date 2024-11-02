package com.prac.local.local

import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.impl.RemoteKeyLocalDataSourceImpl
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.entity.RemoteKey
import com.prac.shared_test.local.room.FakeRemoteKeyDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class RemoteKeyLocalDataSourceTest {

    private lateinit var remoteKeyDao: RemoteKeyDao
    private lateinit var remoteKeyLocalDataSource: RemoteKeyLocalDataSource

    @Before
    fun setUp() {
        remoteKeyDao = FakeRemoteKeyDao()
        remoteKeyLocalDataSource = RemoteKeyLocalDataSourceImpl(remoteKeyDao)
    }

    @Test
    fun remoteKey_existingID_remoteKey() = runTest {
        val remoteKeys = makeRemoteKeys()
        remoteKeyLocalDataSource.insertRemoteKeys(remoteKeys)
        val index = 0
        val remoteKey = remoteKeys[index]

        val result = remoteKeyLocalDataSource.remoteKey(remoteKey.repoId)

        Assert.assertEquals(result, remoteKey)
    }

    @Test
    fun remoteKey_notExistingID_null() = runTest {
        val randomID = Random.nextInt(100)

        val result = remoteKeyLocalDataSource.remoteKey(randomID)

        assertNull(result)
    }

    @Test
    fun insertRemoteKeys_insertRemoteKeys_remoteKeys() = runTest {
        val remoteKeys = makeRemoteKeys()
        val expectedSize = remoteKeys.size

        remoteKeyLocalDataSource.insertRemoteKeys(remoteKeys)

        repeat(expectedSize) {
            val result = remoteKeyLocalDataSource.remoteKey(remoteKeys[it].repoId)
            assertEquals(remoteKeys[it], result)
        }
    }

    @Test
    fun clearRemoteKeys_clearRoom_emptyList() = runTest {
        val remoteKeys = makeRemoteKeys()
        remoteKeyLocalDataSource.insertRemoteKeys(remoteKeys)
        val expectedSize = remoteKeys.size

        remoteKeyLocalDataSource.clearRemoteKeys()

        repeat(expectedSize) {
            val result = remoteKeyLocalDataSource.remoteKey(remoteKeys[it].repoId)
            assertNull(result)
        }
    }

    private fun makeRemoteKeys() =
        listOf(
            RemoteKey(repoId = 0, prevKey = null, nextKey = 2),
            RemoteKey(repoId = 1, prevKey = null, nextKey = 2),
        )
}