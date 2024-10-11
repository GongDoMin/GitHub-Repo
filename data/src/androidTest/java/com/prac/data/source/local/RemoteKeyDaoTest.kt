package com.prac.data.source.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prac.data.source.local.room.dao.RemoteKeyDao
import com.prac.data.source.local.room.database.RepositoryDatabase
import com.prac.data.source.local.room.entity.RemoteKey
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
    fun remoteKey_existingId_returnCorrectRemoteKey() = runTest {
        val index = 0
        val remoteKeys = makeRemoteKeys()
        remoteKeyDao.insertRemoteKeys(remoteKeys)

        val result = remoteKeyDao.remoteKey(remoteKeys[index].repoId)

        assertEquals(result, remoteKeys[index])
    }

    @Test
    fun remoteKey_nonExistingId_returnNull() = runTest {

        val result = remoteKeyDao.remoteKey(Random.nextInt(100))

        assertNull(result)
    }

    private fun makeRemoteKeys() =
        listOf(
            RemoteKey(repoId = 0, prevKey = null, nextKey = 2),
            RemoteKey(repoId = 1, prevKey = null, nextKey = 2),
        )
}