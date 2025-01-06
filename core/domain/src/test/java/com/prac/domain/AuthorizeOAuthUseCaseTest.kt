package com.prac.domain

import com.prac.domain.impl.AuthorizeOAuthUseCaseImpl
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.data.FakeUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthorizeOAuthUseCaseTest {

    private val userName = "test"

    private lateinit var tokenRepository: FakeTokenRepository
    private lateinit var userRepository: FakeUserRepository

    private lateinit var authorizeOAuthUseCase: AuthorizeOAuthUseCase

    @Before
    fun setUp() {
        tokenRepository = FakeTokenRepository("accessToken")
        userRepository = FakeUserRepository(userName)

        authorizeOAuthUseCase = AuthorizeOAuthUseCaseImpl(tokenRepository, userRepository)
    }

    @Test
    fun invoke_whenNotError_returnIsSuccess_and_updateUserName() = runTest {
        val result = authorizeOAuthUseCase.invoke("success")

        assertTrue(result.isSuccess)
        assertEquals(userRepository.getLocalUserName(), userName)
    }

    @Test
    fun invoke_whenError_returnIsFail() = runTest {
        val result = authorizeOAuthUseCase.invoke("ioException")

        assertTrue(result.isFailure)
        assertTrue(userRepository.getLocalUserName().isEmpty())
    }
}