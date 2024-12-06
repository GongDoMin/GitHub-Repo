package com.prac.feature.main

import androidx.paging.PagingData
import app.cash.turbine.test
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.UNKNOWN
import com.prac.data.model.RepoModel
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.rules.StandardTestDispatcherRule
import com.prac.shared_test.common.FakeBackOffWorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private val tokenRepository: TokenRepository = FakeTokenRepository("test")
    @Mock private lateinit var mockRepoRepository: RepoRepository
    private val backOffWork: FakeBackOffWorkManager = FakeBackOffWorkManager()
    private val mainReducerProcessor = MainReducerProcessor()

    private lateinit var mainViewModel: MainViewModel

    private val repositories = listOf(RepoModel(stargazersCount = 1))

    @Before
    fun setUp() = runTest {
        val pagingData = PagingData.from(repositories)
        whenever(mockRepoRepository.getRepositories()).thenReturn(flow { emit(pagingData) } )

        mainViewModel = MainViewModel(mockRepoRepository, tokenRepository, backOffWork, mainReducerProcessor, standardTestDispatcherRule.testDispatcher)
    }

    @After
    fun tearDown() {
        backOffWork.clearWork()
        backOffWork.clearDelayTimes()
    }

    @Test
    fun starRepository_starRepositoryIsSuccess_callStarLocalAndRemoteRepository() = runTest {
        val repository = repositories[0]

        mainViewModel.process(Action.UserAction.OnClickUnStar(repository))
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repository.id, repository.stargazersCount + 1)
        verify(mockRepoRepository).starRepository(repository.owner.login, repository.name)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsSuccess_callStarLocalAndRemoteRepository() = runTest {
        val repository = repositories[0]

        mainViewModel.process(Action.UserAction.OnClickStar(repository))
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repository.id, repository.stargazersCount - 1)
        verify(mockRepoRepository).unStarRepository(repository.owner.login, repository.name)
    }

    @Test
    fun starRepository_starRepositoryIsNetworkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repository = repositories[0]
        val uniqueID = "star_${repository.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.starRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        mainViewModel.process(Action.UserAction.OnClickUnStar(repository))
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repository.id, repository.stargazersCount + 1)
        verify(mockRepoRepository, times(expectedCallTimes)).starRepository(repository.owner.login, repository.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsNetworkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repository = repositories[0]
        val uniqueID = "star_${repository.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.unStarRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        mainViewModel.process(Action.UserAction.OnClickStar(repository))
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repository.id, repository.stargazersCount - 1)
        verify(mockRepoRepository, times(expectedCallTimes)).unStarRepository(repository.owner.login, repository.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun starRepository_starRepositoryIsAuthorizationError_eventIsLogout() = runTest {
        val repository = repositories[0]
        whenever(mockRepoRepository.starRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        mainViewModel.eventFlow.test {
            mainViewModel.process(Action.UserAction.OnClickUnStar(repository))

            val result = awaitItem()
            assertTrue(result is Event.Logout)
        }
    }

    @Test
    fun unStarRepository_unStarRepositoryIsAuthorizationError_uiStateHasDialogMessage() = runTest {
        val repository = repositories[0]
        whenever(mockRepoRepository.unStarRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        mainViewModel.eventFlow.test {
            mainViewModel.process(Action.UserAction.OnClickStar(repository))

            val result = awaitItem()
            assertTrue(result is Event.Logout)
        }
    }

    @Test
    fun starRepository_starRepositoryIsNotFoundRepositoryError_uiStateIsError() = runTest {
        val repository = repositories[0]
        whenever(mockRepoRepository.starRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickUnStar(repository))

            val result = awaitItem()
            assertEquals(result.errorMessage, INVALID_REPOSITORY)
            verify(mockRepoRepository).unStarLocalRepository(repository.id, repository.stargazersCount)
        }
    }

    @Test
    fun unStarRepository_unStarRepositoryIsNotFoundRepositoryError_uiStateIsError() = runTest {
        val repository = repositories[0]
        whenever(mockRepoRepository.unStarRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickStar(repository))

            val result = awaitItem()
            assertEquals(result.errorMessage, INVALID_REPOSITORY)
            verify(mockRepoRepository).starLocalRepository(repository.id, repository.stargazersCount)
        }
    }

    @Test
    fun starRepository_starRepositoryIsUnKnownError_uiStateIsError() = runTest {
        val repository = repositories[0]
        whenever(mockRepoRepository.starRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickUnStar(repository))

            val result = awaitItem()
            assertEquals(result.errorMessage, UNKNOWN)
            verify(mockRepoRepository).unStarLocalRepository(repository.id, repository.stargazersCount)
        }
    }

    @Test
    fun unStarRepository_unStarRepositoryIsUnKnownError_uiStateIsError() = runTest {
        val repository = repositories[0]
        whenever(mockRepoRepository.unStarRepository(repository.owner.login, repository.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickStar(repository))

            val result = awaitItem()
            assertEquals(result.errorMessage, UNKNOWN)
            verify(mockRepoRepository).starLocalRepository(repository.id, repository.stargazersCount)
        }
    }
}