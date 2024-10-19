package com.prac.data.source.local

import com.prac.data.fake.source.local.FakeUserDataStoreManager
import com.prac.data.source.local.datastore.user.UserDataStoreManager
import com.prac.data.source.local.impl.UserLocalDataSourceImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserLocalDataSourceTest {

    private lateinit var userDataStoreManager: UserDataStoreManager
    private lateinit var userLocalDataSource: UserLocalDataSource

    @Before
    fun setUp() {
        userDataStoreManager = FakeUserDataStoreManager()
        userLocalDataSource = UserLocalDataSourceImpl(userDataStoreManager)
    }

    @Test
    fun getUserName_userNamePassedToDataSource() = runTest {
        val expectedUserName = ""

        val result = userLocalDataSource.getUserName()

        assertEquals(result, expectedUserName)
    }

    @Test
    fun setUserName_returnExpectedUserName() = runTest {
        val expectedUserName = "test"

        userLocalDataSource.setUserName(expectedUserName)

        val result = userLocalDataSource.getUserName()
        assertEquals(result, expectedUserName)
    }

    @Test
    fun clearUserName_returnEmptyString() = runTest {
        val userName = "test"
        userLocalDataSource.setUserName(userName)

        userLocalDataSource.clearUserName()

        val result = userLocalDataSource.getUserName()
        assertTrue(result.isEmpty())
    }
}