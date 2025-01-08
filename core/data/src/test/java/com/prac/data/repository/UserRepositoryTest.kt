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

    private lateinit var userApiDataSource: UserApiDataSource
    private lateinit var userLocalDataSource: UserLocalDataSource

    private lateinit var userRepository: UserRepository

    @Test
    fun API사용자이름가져오기_사용자이름_반환() = runTest {
        // given
        val expectedUserName = "Son"
        initialUserRepository(
            userName = expectedUserName
        )

        // when
        val result = userRepository.getApiUserName("accessToken")

        // then
        assertEquals(result, expectedUserName)
    }

    @Test
    fun 사용자이름저장_저장된이름_반환() = runTest {
        // given
        val expectedUserName = "kane"
        initialUserRepository(
            userName = expectedUserName
        )

        // when
        userRepository.setUserName(expectedUserName)

        // then
        val result = userRepository.getLocalUserName()
        assertEquals(result, expectedUserName)
    }

    @Test
    fun 사용자이름초기화_빈값_반환() = runTest {
        // given
        val expectedUserName = "salah"
        initialUserRepository(
            userName = expectedUserName
        )
        userRepository.setUserName(expectedUserName)

        // when
        userRepository.clearUserName()

        // then
        val result = userRepository.getLocalUserName()
        assertTrue(result.isEmpty())
    }

    private fun initialUserRepository(
        userName: String
    ) {
       userApiDataSource = FakeUserApiDataSource(
           userName = userName
       )
       userLocalDataSource = FakeUserLocalDataSource()

       userRepository = UserRepositoryImpl(userApiDataSource, userLocalDataSource)
    }

}