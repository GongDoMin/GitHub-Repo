package com.prac.local.local

import com.prac.local.UserLocalDataSource
import com.prac.local.datastore.user.UserDataStoreManager
import com.prac.local.impl.UserLocalDataSourceImpl
import com.prac.shared_test.local.datastore.FakeUserDataStoreManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserLocalDataSourceTest {

    private lateinit var userDataStoreManager: UserDataStoreManager
    private lateinit var userLocalDataSource: UserLocalDataSource

    @Test
    fun 데이터스토어가_비어있을때_빈문자열_반환() = runTest {
        // given
        initialUserLocalDataSourceWithNoting()

        // when
        val result = userLocalDataSource.getUserName()

        // then
        assertTrue(result.isEmpty())
    }

    @Test
    fun 데이터스토어가_비어있지_않을때_유저이름_반환() = runTest {
        // given
        initialUserLocalDataSourceWithFakeUserName()

        // when
        val result = userLocalDataSource.getUserName()

        // then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun 유저이름_갱신_유저이름_반환() = runTest {
        // given
        initialUserLocalDataSourceWithNoting()

        // when
        userLocalDataSource.setUserName(FAKE_USER_NAME)

        // then
        val result = userLocalDataSource.getUserName()
        assertEquals(result, FAKE_USER_NAME)
    }

    @Test
    fun 유저이름_초기화_빈문자열_반환() = runTest {
        // given
        initialUserLocalDataSourceWithFakeUserName()

        // when
        userLocalDataSource.clearUserName()

        // then
        val result = userLocalDataSource.getUserName()
        assertTrue(result.isEmpty())
    }

    private fun initialUserLocalDataSourceWithNoting() {
        userDataStoreManager = FakeUserDataStoreManager()
        userLocalDataSource = UserLocalDataSourceImpl(userDataStoreManager)
    }

    private fun initialUserLocalDataSourceWithFakeUserName() {
        userDataStoreManager = FakeUserDataStoreManager(FAKE_USER_NAME)
        userLocalDataSource = UserLocalDataSourceImpl(userDataStoreManager)
    }

    companion object {
        private const val FAKE_USER_NAME = "GongDoMin"
    }
}