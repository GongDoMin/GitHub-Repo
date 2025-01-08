package com.prac.local.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prac.local.model.RemoteKeyEntity
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.database.RepositoryDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random
import kotlin.random.nextInt

@RunWith(AndroidJUnit4::class)
class RemoteKeyDaoTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var repositoryDatabase: RepositoryDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao

    @Before
    fun 초기화() {
        repositoryDatabase =
            Room
                .inMemoryDatabaseBuilder(context, RepositoryDatabase::class.java)
                .build()
        remoteKeyDao = repositoryDatabase.remoteKeyDao()
    }

    @After
    fun 정리() = runTest {
        remoteKeyDao.clearRemoteKeys()
        repositoryDatabase.close()
    }

    @Test
    fun 키_입력_정상적으로_입력되는지_확인() = runTest {
        // when
        remoteKeyDao.insertRemoteKeys(fakeRemoteKeys)

        // then
        fakeRemoteKeys.indices.forEach { index ->
            val result = remoteKeyDao.remoteKey(fakeRemoteKeys[index].repoId)
            assertEquals(result, fakeRemoteKeys[index])
        }
    }

    @Test
    fun 존재하는_키_조회_키_반환() = runTest {
        // given
        val index = fakeRemoteKeys.size - 1
        val expectedRemoteKey = fakeRemoteKeys[index]
        remoteKeyDao.insertRemoteKeys(fakeRemoteKeys)

        // when
        val result = remoteKeyDao.remoteKey(expectedRemoteKey.repoId)

        // then
        assertEquals(result, expectedRemoteKey)
    }

    @Test
    fun 존재하지_않는_키_조회_널_반환() = runTest {
        // given
        val randomID = Random.nextInt(IntRange(100, 1000))
        remoteKeyDao.insertRemoteKeys(fakeRemoteKeys)

        // when
        val result = remoteKeyDao.remoteKey(randomID)

        // then
        assertNull(result)
    }

    @Test
    fun 키삭제_널_반환() = runTest {
        // given
        remoteKeyDao.insertRemoteKeys(fakeRemoteKeys)

        // when
        remoteKeyDao.clearRemoteKeys()

        // then
        fakeRemoteKeys.indices.forEach { index ->
            val result = remoteKeyDao.remoteKey(fakeRemoteKeys[index].repoId)
            assertNull(result)
        }
    }

    companion object {
        private val fakeRemoteKeys =
            listOf(
                RemoteKeyEntity(repoId = 0, prevKey = null, nextKey = null),
                RemoteKeyEntity(repoId = 1, prevKey = null, nextKey = null),
            )
    }
}