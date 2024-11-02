package com.prac.local.local

import com.prac.local.UserLocalDataSource
import com.prac.local.impl.UserLocalDataSourceImpl
import com.prac.shared_test.local.datastore.FakeUserDataStoreManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserLocalDataSourceTest {

    private lateinit var userDataStoreManager: FakeUserDataStoreManager
    private lateinit var userLocalDataSource: UserLocalDataSource

    private val initialUserName = "test"

    @Test
    fun getUserName_dataStoreIsEmpty_emptyUserName() = runTest {
        userDataStoreManager = FakeUserDataStoreManager()
        userLocalDataSource = UserLocalDataSourceImpl(userDataStoreManager)

        val result = userLocalDataSource.getUserName()

        assertTrue(result.isEmpty())
    }

    @Test
    fun getUserName_dataStoreIsNotEmpty_notEmptyUserName() = runTest {
        userDataStoreManager = FakeUserDataStoreManager(initialUserName)
        userLocalDataSource = UserLocalDataSourceImpl(userDataStoreManager)

        val result = userLocalDataSource.getUserName()

        assertTrue(result.isNotEmpty())
    }

    @Test
    fun setUserName_updateNewUserName_cacheAndLocalNotEmptyUserName() = runTest {
        val expectedUserName = "test"
        userDataStoreManager = FakeUserDataStoreManager()
        userLocalDataSource = UserLocalDataSourceImpl(userDataStoreManager)

        userLocalDataSource.setUserName(expectedUserName)

        val cache = userLocalDataSource.getUserName()
        val local = userDataStoreManager.getUserName()
        assertEquals(cache, expectedUserName)
        assertEquals(local, expectedUserName)
    }

    @Test
    fun clearUserName_updateEmptyUserName_cacheAndLocalEmptyUserName() = runTest {
        userDataStoreManager = FakeUserDataStoreManager(initialUserName)
        userLocalDataSource = UserLocalDataSourceImpl(userDataStoreManager)

        userLocalDataSource.clearUserName()

        val cache = userLocalDataSource.getUserName()
        val local = userDataStoreManager.getUserName()
        assertTrue(cache.isEmpty())
        assertTrue(local.isEmpty())
    }
}