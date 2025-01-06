package com.prac.domain

import com.prac.domain.impl.ClearTokenUseCaseImpl
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.data.FakeUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClearTokenUseCaseTest {

    private val userName = "test"

    private val tokenRepository = FakeTokenRepository("accessToken")
    private val userRepository = FakeUserRepository(userName)

    private val clearTokenUseCase = ClearTokenUseCaseImpl(tokenRepository, userRepository)

    @Test
    fun invoke_clearToken_and_userName() = runTest {
        clearTokenUseCase.invoke()

        assertFalse(tokenRepository.isLoggedIn())
        assertTrue(userRepository.getLocalUserName().isEmpty())
    }
}