package com.prac.feature.main

import androidx.paging.PagingData
import app.cash.turbine.test
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.UNKNOWN
import com.prac.data.model.OwnerModel
import com.prac.data.model.RepoModel
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
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

    @Before
    fun setUp() = runTest {
        val pagingData = PagingData.from(emptyList<RepoModel>())
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
        val repoEntity = makeRepoEntity()

        mainViewModel.process(Action.UserAction.OnClickUnStar(repoEntity))
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount + 1)
        verify(mockRepoRepository).starRepository(repoEntity.owner.login, repoEntity.name)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsSuccess_callStarLocalAndRemoteRepository() = runTest {
        val repoEntity = makeRepoEntity()

        mainViewModel.process(Action.UserAction.OnClickStar(repoEntity))
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount - 1)
        verify(mockRepoRepository).unStarRepository(repoEntity.owner.login, repoEntity.name)
    }

    @Test
    fun starRepository_starRepositoryIsNetworkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repoEntity = makeRepoEntity()
        val uniqueID = "star_${repoEntity.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.NetworkError()))

        mainViewModel.process(Action.UserAction.OnClickUnStar(repoEntity))
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount + 1)
        verify(mockRepoRepository, times(expectedCallTimes)).starRepository(repoEntity.owner.login, repoEntity.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsNetworkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repoEntity = makeRepoEntity()
        val uniqueID = "star_${repoEntity.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.NetworkError()))

        mainViewModel.process(Action.UserAction.OnClickStar(repoEntity))
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount - 1)
        verify(mockRepoRepository, times(expectedCallTimes)).unStarRepository(repoEntity.owner.login, repoEntity.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun starRepository_starRepositoryIsAuthorizationError_eventIsLogout() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.AuthorizationError()))

        mainViewModel.process(Action.UserAction.OnClickUnStar(repoEntity))

        mainViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.Logout)
        }
    }

    @Test
    fun unStarRepository_unStarRepositoryIsAuthorizationError_uiStateHasDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.AuthorizationError()))

        mainViewModel.process(Action.UserAction.OnClickStar(repoEntity))

        mainViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.Logout)
        }
    }

    @Test
    fun starRepository_starRepositoryIsNotFoundRepositoryError_uiStateIsError() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(com.prac.exception.RepositoryException.NotFoundRepository()))

        mainViewModel.process(Action.UserAction.OnClickUnStar(repoEntity))

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            val result = awaitItem()
            assertEquals(result.errorMessage, INVALID_REPOSITORY)
            verify(mockRepoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)
        }
    }

    @Test
    fun unStarRepository_unStarRepositoryIsNotFoundRepositoryError_uiStateIsError() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(com.prac.exception.RepositoryException.NotFoundRepository()))

        mainViewModel.process(Action.UserAction.OnClickStar(repoEntity))

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            val result = awaitItem()
            assertEquals(result.errorMessage, INVALID_REPOSITORY)
            verify(mockRepoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount)
        }
    }

    @Test
    fun starRepository_starRepositoryIsUnKnownError_uiStateIsError() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.UnKnownError()))

        mainViewModel.process(Action.UserAction.OnClickUnStar(repoEntity))

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            val result = awaitItem()
            assertEquals(result.errorMessage, UNKNOWN)
            verify(mockRepoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)
        }
    }

    @Test
    fun unStarRepository_unStarRepositoryIsUnKnownError_uiStateIsError() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.UnKnownError()))

        mainViewModel.process(Action.UserAction.OnClickStar(repoEntity))

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            val result = awaitItem()
            assertEquals(result.errorMessage, UNKNOWN)
            verify(mockRepoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount)
        }
    }

    private fun makeRepoEntity() =
        RepoModel(
            id = 1,
            name = "name",
            owner = OwnerModel(login = "login", avatarUrl = "avatarUrl"),
            stargazersCount = 10,
            defaultBranch = "master",
            updatedAt = "updatedAt",
            isStarred = null
        )
}