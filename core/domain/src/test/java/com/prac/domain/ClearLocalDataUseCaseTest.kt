package com.prac.domain

import com.prac.data.repository.RepoRepository
import com.prac.domain.impl.ClearLocalDataUseCaseImpl
import com.prac.shared_test.data.FakeRepoRepository
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.data.FakeUserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class ClearLocalDataUseCaseTest {

    private val userName = "test"

    private val tokenRepository = FakeTokenRepository("accessToken")
    private val userRepository = FakeUserRepository(userName)
    @Mock private lateinit var repoRepository: RepoRepository

    private lateinit var clearLocalDataUseCase: ClearLocalDataUseCase

    @Test
    fun invoke_clearToken_and_userName() = runTest {
        clearLocalDataUseCase = ClearLocalDataUseCaseImpl(tokenRepository, userRepository, repoRepository)

        clearLocalDataUseCase.invoke()

        assertFalse(tokenRepository.isLoggedIn())
        assertTrue(userRepository.getLocalUserName().isEmpty())
        verify(repoRepository).clearRepositories()
    }
}