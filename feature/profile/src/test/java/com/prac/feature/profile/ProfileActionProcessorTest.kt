package com.prac.feature.profile

import app.cash.turbine.test
import com.prac.data.repository.RepoRepository
import com.prac.domain.ClearLocalDataUseCase
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event
import com.prac.feature.profile.model.Mutation
import com.prac.shared_test.common.FakeBackOffWorkManager
import com.prac.shared_test.data.FakeTokenRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class ProfileActionProcessorTest {

    private val tokenRepository: FakeTokenRepository = FakeTokenRepository("test")
    @Mock private lateinit var mockRepoRepository: RepoRepository
    @Mock private lateinit var mockClearLocalDataUseCase: ClearLocalDataUseCase
    private val backOffWork: FakeBackOffWorkManager = FakeBackOffWorkManager()
    private lateinit var profileActionProcessor: ProfileActionProcessor

    @Before
    fun setUp() {
        profileActionProcessor = ProfileActionProcessor(
            tokenRepository = tokenRepository,
            repoRepository = mockRepoRepository,
            clearLocalDataUseCase = mockClearLocalDataUseCase,
            backOffWorkManager = backOffWork
        )
    }

    @Test
    fun invoke_actionIsOnClickLogoutButton_mutationIsShowDialog() = runTest {
        profileActionProcessor(Action.UserAction.OnClickLogoutButton).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowDialog)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsDialogDismiss_mutationIsShowIdle() = runTest {
        profileActionProcessor(Action.UserAction.DialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowIdle)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsOnClickNegativeButton_mutationIsShowIdle() = runTest {
        profileActionProcessor(Action.UserAction.OnClickNegativeButton).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowIdle)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsOnClickPositiveButton_mutationIsShowIdle() = runTest {
        profileActionProcessor(Action.UserAction.OnClickPositiveButton).test {
            awaitItem() // isLoading

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Logout)
        }

        assertTrue(backOffWork.getWorkSize() == 0)
        verify(mockRepoRepository).clearRepositories()
        verify(mockClearLocalDataUseCase).invoke()
    }
}