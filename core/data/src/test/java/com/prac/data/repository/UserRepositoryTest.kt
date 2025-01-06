package com.prac.data.repository

import com.prac.data.impl.UserRepositoryImpl
import com.prac.local.UserLocalDataSource
import com.prac.network.UserApiDataSource
import com.prac.shared_test.local.source.FakeUserLocalDataSource
import com.prac.shared_test.network.FakeUserApiDataSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryTest {

    private val apiUserName = "test"

    private val userApiDataSource: UserApiDataSource = FakeUserApiDataSource()
    private val userLocalDataSource: UserLocalDataSource = FakeUserLocalDataSource()

    private val userRepository: UserRepository = UserRepositoryImpl(userApiDataSource, userLocalDataSource)

    @Test
    fun getApiUserName_returnUserName() = runTest {
        val result = userRepository.getApiUserName("test")

        assertEquals(result, apiUserName)
    }

    @Test
    fun setUserName_saveUserName() = runTest {
        val userName = userRepository.getApiUserName("test")
        userRepository.setUserName(userName)

        val result = userRepository.getLocalUserName()

        assertEquals(result, apiUserName)
    }

    @Test
    fun clearUserName_returnEmpty() = runTest {
        val userName = userRepository.getApiUserName("test")
        userRepository.setUserName(userName)
        userRepository.getLocalUserName()

        userRepository.clearUserName()

        val result = userRepository.getLocalUserName()
        assertTrue(result.isEmpty())
    }
}