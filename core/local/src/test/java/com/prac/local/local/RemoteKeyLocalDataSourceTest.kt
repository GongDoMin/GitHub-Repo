package com.prac.local.local

import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.impl.RemoteKeyLocalDataSourceImpl
import com.prac.local.model.RemoteKeyEntity
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.shared_test.local.room.FakeRemoteKeyDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteKeyLocalDataSourceTest {

    private val remoteKeyDao: RemoteKeyDao = FakeRemoteKeyDao()
    private val remoteKeyLocalDataSource: RemoteKeyLocalDataSource = RemoteKeyLocalDataSourceImpl(remoteKeyDao)

    @After
    fun 정리() = runTest {
        remoteKeyDao.clearRemoteKeys()
    }

    @Test
    fun 키_입력_정상적으로_입력되는지_확인() = runTest {
        // when
        remoteKeyLocalDataSource.insertRemoteKeys(fakeRemoteKeys)

        // then
        fakeRemoteKeys.indices.forEach { index ->
            val result = remoteKeyLocalDataSource.remoteKey(fakeRemoteKeys[index].repoId)
            assertEquals(fakeRemoteKeys[index], result)
        }
    }

    @Test
    fun 존재하는_키_조회_키_반환() = runTest {
        // given
        val index = fakeRemoteKeys.indices.first
        val expectedRemoteKey = fakeRemoteKeys[index]
        remoteKeyLocalDataSource.insertRemoteKeys(fakeRemoteKeys)

        // when
        val result = remoteKeyLocalDataSource.remoteKey(expectedRemoteKey.repoId)

        // then
        assertEquals(result, expectedRemoteKey)
    }

    @Test
    fun 존재하지_않는_키_조회_널_반환() = runTest {
        // given
        val id = fakeRemoteKeys.maxOf { it.repoId } + 1
        remoteKeyLocalDataSource.insertRemoteKeys(fakeRemoteKeys)

        // when
        val result = remoteKeyLocalDataSource.remoteKey(id)

        // then
        assertNull(result)
    }

    @Test
    fun 키삭제_널_반환() = runTest {
        // given
        remoteKeyLocalDataSource.insertRemoteKeys(fakeRemoteKeys)

        // when
        remoteKeyLocalDataSource.clearRemoteKeys()

        // then
        fakeRemoteKeys.indices.forEach { index ->
            val result = remoteKeyLocalDataSource.remoteKey(fakeRemoteKeys[index].repoId)
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