package com.prac.feature.main

import app.cash.turbine.test
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.repository.RepoRepository
import com.prac.domain.ClearTokenUseCase
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.model.Repository
import com.prac.shared_test.common.FakeBackOffWorkManager
import com.prac.shared_test.data.FakeTokenRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainActionProcessorTest {

    private val tokenRepository: FakeTokenRepository = FakeTokenRepository("test")
    @Mock private lateinit var mockRepoRepository: RepoRepository
    @Mock private lateinit var mockClearTokenUseCase: ClearTokenUseCase
    private val backOffWork: FakeBackOffWorkManager = FakeBackOffWorkManager()
    private lateinit var mainActionProcessor: MainActionProcessor

    @Before
    fun setUp() {
        mainActionProcessor = MainActionProcessor(
            tokenRepository = tokenRepository,
            repoRepository = mockRepoRepository,
            clearTokenUseCase = mockClearTokenUseCase,
            backOffWorkManager = backOffWork
        )
    }

    @Test
    fun invoke_actionIsLoad_emitNothing() = runTest {
        mainActionProcessor(Action.InternalAction.Load).test {
            awaitComplete()
        }
    }

    @Test
    fun invoke_actionIsFetchStarState_emitNothing() = runTest {
        val repository = Repository()
        whenever(mockRepoRepository.isStarred(repository.id, repository.name)).thenReturn(Unit)

        mainActionProcessor(Action.InternalAction.FetchStarState(repository)).test {
            awaitComplete()
        }

        verify(mockRepoRepository).isStarred(repository.id, repository.name)
    }

    @Test
    fun invoke_actionIsLogout_mutationIsError() = runTest {
        whenever(mockRepoRepository.clearRepositories()).thenReturn(Unit)

        mainActionProcessor(Action.InternalAction.Logout).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue((mutation as Mutation.ShowError).errorMessage == INVALID_TOKEN)
            assertTrue(event == null)
        }

        verify(mockRepoRepository).clearRepositories()
        verify(mockClearTokenUseCase).invoke()
        assertTrue(backOffWork.getWorkSize() == 0)
    }

    @Test
    fun invoke_actionIsOnClickRepository_eventIsOpenRepositoryDetail() = runTest {
        mainActionProcessor(Action.UserAction.OnClickRepository(Repository())).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.OpenRepositoryDetail)
        }
    }

    @Test
    fun invoke_actionIsOnClickUnStar_emitNothing_whenSuccess() = runTest {
        val repository = Repository()
        mainActionProcessor(Action.UserAction.OnClickUnStar(repository)).test {
            awaitComplete()
        }

        verify(mockRepoRepository).starLocalRepository(repository.id, repository.stargazersCount + 1)
        verify(mockRepoRepository).starRepository(repository.owner.login, repository.name)
    }

    @Test
    fun invoke_actionIsOnClickStar_emitNothing_whenSuccess() = runTest {
        val repository = Repository()
        mainActionProcessor(Action.UserAction.OnClickStar(repository)).test {
            awaitComplete()
        }

        verify(mockRepoRepository).unStarLocalRepository(repository.id, repository.stargazersCount - 1)
        verify(mockRepoRepository).unStarRepository(repository.owner.login, repository.name)
    }

    @Test
    fun invoke_actionIsOnClickUnStar_emitNothing_whenNetworkError() = runTest {
        backOffWork.setScope(this)
        val repository = Repository()
        val uniqueID = "star_${repository.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.starRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        mainActionProcessor(Action.UserAction.OnClickUnStar(repository)).test {
            awaitComplete()
        }

        advanceUntilIdle()
        verify(mockRepoRepository).starLocalRepository(repository.id, repository.stargazersCount + 1)
        verify(mockRepoRepository, times(expectedCallTimes)).starRepository(repository.owner.login, repository.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun invoke_actionIsOnClickStar_emitNothing_whenNetworkError() = runTest {
        backOffWork.setScope(this)
        val repository = Repository()
        val uniqueID = "star_${repository.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.unStarRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        mainActionProcessor(Action.UserAction.OnClickStar(repository)).test {
            awaitComplete()
        }

        advanceUntilIdle()
        verify(mockRepoRepository).unStarLocalRepository(repository.id, repository.stargazersCount - 1)
        verify(mockRepoRepository, times(expectedCallTimes)).unStarRepository(repository.owner.login, repository.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun invoke_actionIsOnClickUnStar_mutationIsLogout_whenAuthorizationError() = runTest {
        val repository = Repository()
        whenever(mockRepoRepository.starRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        mainActionProcessor(Action.UserAction.OnClickUnStar(repository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsOnClickStar_mutationIsLogout_whenAuthorizationError() = runTest {
        val repository = Repository()
        whenever(mockRepoRepository.unStarRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        mainActionProcessor(Action.UserAction.OnClickStar(repository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsOnClickUnStar_mutationIsError_whenRepositoryNotFound() = runTest {
        val repository = Repository()
        whenever(mockRepoRepository.starRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        mainActionProcessor(Action.UserAction.OnClickUnStar(repository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
            verify(mockRepoRepository).unStarLocalRepository(repository.id, repository.stargazersCount)
        }
    }

    @Test
    fun invoke_actionIsOnClickStar_mutationIsError_whenRepositoryNotFound() = runTest {
        val repository = Repository()
        whenever(mockRepoRepository.unStarRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        mainActionProcessor(Action.UserAction.OnClickStar(repository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
            verify(mockRepoRepository).starLocalRepository(repository.id, repository.stargazersCount)
        }
    }

    @Test
    fun invoke_actionIsOnClickUnStar_mutationIsError_whenUnknownError() = runTest {
        val repository = Repository()
        whenever(mockRepoRepository.starRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        mainActionProcessor(Action.UserAction.OnClickUnStar(repository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
            verify(mockRepoRepository).unStarLocalRepository(repository.id, repository.stargazersCount)
        }
    }

    @Test
    fun invoke_actionIsOnClickStar_mutationIsError_whenUnknownError() = runTest {
        val repository = Repository()
        whenever(mockRepoRepository.unStarRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        mainActionProcessor(Action.UserAction.OnClickStar(repository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
            verify(mockRepoRepository).starLocalRepository(repository.id, repository.stargazersCount)
        }
    }

    @Test
    fun invoke_actionIsOnClickRetry_eventIsRetry() = runTest {
        mainActionProcessor(Action.UserAction.OnClickRetry).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Retry)
        }
    }

    @Test
    fun invoke_actionIsDialogDismiss_mutationIsShowRepositories() = runTest {
        mainActionProcessor(Action.UserAction.DialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowContent)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsLogoutDialogDismiss_eventIsLogout() = runTest {
        mainActionProcessor(Action.UserAction.LogoutDialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Logout)
        }
    }
}