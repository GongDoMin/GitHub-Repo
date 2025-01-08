package com.prac.domain

import com.prac.data.repository.TokenRepository
import com.prac.data.repository.UserRepository
import com.prac.domain.impl.AuthorizeOAuthUseCaseImpl
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.data.FakeUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthorizeOAuthUseCaseTest {

    private val userName = "son"

    private val tokenRepository: TokenRepository = FakeTokenRepository("accessToken")
    private val userRepository: UserRepository = FakeUserRepository(userName)

    private val authorizeOAuthUseCase: AuthorizeOAuthUseCase =AuthorizeOAuthUseCaseImpl(tokenRepository, userRepository)

    @Test
    fun 에러없을때_성공_반환() = runTest {
        // when
        val result = authorizeOAuthUseCase.invoke("success")

        // then
        assertTrue(result.isSuccess)
        assertEquals(userRepository.getLocalUserName(), userName)
    }

    @Test
    fun 에러발생시_실패_반환() = runTest {
        // when
        val result = authorizeOAuthUseCase.invoke("ioException")

        // then
        assertTrue(result.isFailure)
        assertTrue(userRepository.getLocalUserName().isEmpty())
    }
}