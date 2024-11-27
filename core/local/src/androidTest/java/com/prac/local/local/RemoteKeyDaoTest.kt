package com.prac.local.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.database.RepositoryDatabase
import com.prac.local.room.entity.RemoteKey
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class RemoteKeyDaoTest {

    private lateinit var repositoryDatabase: RepositoryDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        repositoryDatabase = Room.inMemoryDatabaseBuilder(context, RepositoryDatabase::class.java)
            .build()
        remoteKeyDao = repositoryDatabase.remoteKeyDao()
    }

    @After
    fun tearDown() {
        repositoryDatabase.close()
    }

    @Test
    fun remoteKey_existingID_remoteKey() = runTest {
        val remoteKeys = makeRemoteKeys()
        remoteKeyDao.insertRemoteKeys(remoteKeys)
        val index = 0
        val remoteKey = remoteKeys[index]

        val result = remoteKeyDao.remoteKey(remoteKey.repoId)

        assertEquals(result, remoteKey)
    }

    @Test
    fun remoteKey_notExistingID_null() = runTest {
        val randomID = Random.nextInt(100)

        val result = remoteKeyDao.remoteKey(randomID)

        assertNull(result)
    }

    @Test
    fun insertRemoteKeys_insertRemoteKeys_remoteKeys() = runTest {
        val remoteKeys = makeRemoteKeys()
        val expectedSize = remoteKeys.size

        remoteKeyDao.insertRemoteKeys(remoteKeys)

        repeat(expectedSize) {
            val result = remoteKeyDao.remoteKey(remoteKeys[it].repoId)
            assertEquals(remoteKeys[it], result)
        }
    }

    @Test
    fun clearRemoteKeys_clearRoom_emptyList() = runTest {
        val remoteKeys = makeRemoteKeys()
        remoteKeyDao.insertRemoteKeys(remoteKeys)
        val expectedSize = remoteKeys.size

        remoteKeyDao.clearRemoteKeys()

        repeat(expectedSize) {
            val result = remoteKeyDao.remoteKey(remoteKeys[it].repoId)
            assertNull(result)
        }
    }

    private fun makeRemoteKeys() =
        listOf(
            RemoteKey(repoId = 0, prevKey = null, nextKey = 2),
            RemoteKey(repoId = 1, prevKey = null, nextKey = 2),
        )
}