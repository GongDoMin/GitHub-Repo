package com.prac.feature.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoDetailEntity
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.rules.StandardTestDispatcherRule
import com.prac.shared_test.ui.FakeBackOffWorkManager
import com.prac.shared_test.ui.FakeDetailReducerProcessor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
class DetailViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private val tokenRepository: TokenRepository = FakeTokenRepository("test")
    @Mock private lateinit var mockRepoRepository: RepoRepository
    private val detailReducerProcessor = FakeDetailReducerProcessor()
    private val backOffWork: FakeBackOffWorkManager = FakeBackOffWorkManager()

    private lateinit var detailViewModel: DetailViewModel

    @After
    fun tearDown() {
        backOffWork.clearWork()
        backOffWork.clearDelayTimes()
    }

    @Test
    fun getRepository_validInput_uiStateHasRepository() = runTest {
        val userName = "test"
        val repoName = "test"
        val repoDetailEntity = makeRepoDetailEntity()
        val starStateAndCount = Pair(true, 11)
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(mockRepoRepository.getStarStateAndStarCount(repoDetailEntity.id))
            .thenReturn(flow { emit(starStateAndCount) })
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // isLoading == true

            val expectedValue = makeExpectedValue(starStateAndCount.first, starStateAndCount.second)
            val result = awaitItem()
            assertEquals(result.repository, expectedValue)
        }
    }

    @Test
    fun getRepository_invalidInput_uiStateIsError() = runTest {
        initViewModel(null, null)

        detailViewModel.uiStateFlow.test {
            val result = awaitItem()
            assertTrue(result.isError)
            assertTrue(result.errorMessage.isNotEmpty())
        }
    }

    @Test
    fun getRepository_getRepositoryIsNetworkError_uiStateIsError() = runTest {
        val userName = "test"
        val repoName = "test"
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.failure(com.prac.exception.CommonException.NetworkError()))
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // isLoading == true

            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, CONNECTION_FAIL)
        }
    }

    @Test
    fun getRepository_getRepositoryAuthorizationError_uiStateIsError() = runTest {
        val userName = "test"
        val repoName = "test"
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.failure(com.prac.exception.CommonException.AuthorizationError()))
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // isLoading == true

            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, INVALID_TOKEN)
        }
    }

    @Test
    fun starRepository_starRepositoryIsSuccess_callStarLocalAndRemoteRepository() = runTest {
        initViewModel(null, null)
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.success(Unit))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickUnStar(repoDetailEntity))
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount + 1)
        verify(mockRepoRepository).starRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsSuccess_callStarLocalAndRemoteRepository() = runTest {
        initViewModel(null, null)
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.success(Unit))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickStar(repoDetailEntity))
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount - 1)
        verify(mockRepoRepository).unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
    }

    @Test
    fun starRepository_starRepositoryNetworkError_callMultipleTimesRemoteRepository() = runTest {
        initViewModel(null, null)
        backOffWork.setScope(this)
        val repoDetailEntity = makeRepoDetailEntity()
        val uniqueID = "star_${repoDetailEntity.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.NetworkError()))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickUnStar(repoDetailEntity))
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount + 1)
        verify(mockRepoRepository, times(expectedCallTimes)).starRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun unStarRepository_unStarRepositoryNetworkError_callMultipleTimesRemoteRepository() = runTest {
        initViewModel(null, null)
        backOffWork.setScope(this)
        val repoDetailEntity = makeRepoDetailEntity()
        val uniqueID = "star_${repoDetailEntity.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.NetworkError()))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickStar(repoDetailEntity))
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount - 1)
        verify(mockRepoRepository, times(expectedCallTimes)).unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun starRepository_starRepositoryIsAuthorizationError_uiStateHasDialogMessage() = runTest {
        val userName = "test"
        val repoName = "test"
        val repoDetailEntity = makeRepoDetailEntity()
        val starStateAndCount = Pair(true, 11)
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(mockRepoRepository.getStarStateAndStarCount(repoDetailEntity.id))
            .thenReturn(flow { emit(starStateAndCount) })
        initViewModel(userName, repoName)
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.AuthorizationError()))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickUnStar(repoDetailEntity))

        detailViewModel.uiStateFlow.test {
            awaitItem() // isLoading == true
            awaitItem() // showRepository

            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, INVALID_TOKEN)
        }
    }

    @Test
    fun unStarRepository_unStarRepositoryIsAuthorizationError_uiStateHasDialogMessage() = runTest {
        val userName = "test"
        val repoName = "test"
        val repoDetailEntity = makeRepoDetailEntity()
        val starStateAndCount = Pair(true, 11)
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(mockRepoRepository.getStarStateAndStarCount(repoDetailEntity.id))
            .thenReturn(flow { emit(starStateAndCount) })
        initViewModel(userName, repoName)
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.AuthorizationError()))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickStar(repoDetailEntity))

        detailViewModel.uiStateFlow.test {
            awaitItem() // isLoading == true
            awaitItem() // showRepository

            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, INVALID_TOKEN)
        }
    }

    @Test
    fun starRepository_starRepositoryIsNotFoundRepositoryError_uiStateHasDialogMessage() = runTest {
        val userName = "test"
        val repoName = "test"
        val repoDetailEntity = makeRepoDetailEntity()
        val starStateAndCount = Pair(true, 11)
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(mockRepoRepository.getStarStateAndStarCount(repoDetailEntity.id))
            .thenReturn(flow { emit(starStateAndCount) })
        initViewModel(userName, repoName)
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(com.prac.exception.RepositoryException.NotFoundRepository()))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickUnStar(repoDetailEntity))

        detailViewModel.uiStateFlow.test {
            awaitItem() // isLoading == true
            awaitItem() // showRepository

            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, INVALID_REPOSITORY)
        }
    }

    @Test
    fun unStarRepository_unStarRepositoryIsNotFoundRepositoryError_uiStateHasDialogMessage() = runTest {
        val userName = "test"
        val repoName = "test"
        val repoDetailEntity = makeRepoDetailEntity()
        val starStateAndCount = Pair(true, 11)
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(mockRepoRepository.getStarStateAndStarCount(repoDetailEntity.id))
            .thenReturn(flow { emit(starStateAndCount) })
        initViewModel(userName, repoName)
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(com.prac.exception.RepositoryException.NotFoundRepository()))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickStar(repoDetailEntity))

        detailViewModel.uiStateFlow.test {
            awaitItem() // isLoading == true
            awaitItem() // showRepository

            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, INVALID_REPOSITORY)
        }
    }

    @Test
    fun starRepository_starRepositoryIsUnKnownError_uiStateHasDialogMessage() = runTest {
        val userName = "test"
        val repoName = "test"
        val repoDetailEntity = makeRepoDetailEntity()
        val starStateAndCount = Pair(true, 11)
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(mockRepoRepository.getStarStateAndStarCount(repoDetailEntity.id))
            .thenReturn(flow { emit(starStateAndCount) })
        initViewModel(userName, repoName)
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.UnKnownError()))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickUnStar(repoDetailEntity))

        detailViewModel.uiStateFlow.test {
            awaitItem() // isLoading == true
            awaitItem() // showRepository

            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, UNKNOWN)
        }
    }

    @Test
    fun unStarRepository_unStarRepositoryIsUnKnownError_uiStateHasDialogMessage() = runTest {
        val userName = "test"
        val repoName = "test"
        val repoDetailEntity = makeRepoDetailEntity()
        val starStateAndCount = Pair(true, 11)
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(mockRepoRepository.getStarStateAndStarCount(repoDetailEntity.id))
            .thenReturn(flow { emit(starStateAndCount) })
        initViewModel(userName, repoName)
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(com.prac.exception.CommonException.UnKnownError()))

        detailViewModel.process(com.prac.feature.detail.model.Action.UserAction.OnClickStar(repoDetailEntity))

        detailViewModel.uiStateFlow.test {
            awaitItem() // isLoading == true
            awaitItem() // showRepository

            val result = awaitItem()
            assertTrue(result.isError)
            assertEquals(result.errorMessage, UNKNOWN)
        }
    }

    private fun initViewModel(userName: String?, repoName: String?) {
        detailViewModel = com.prac.feature.detail.DetailViewModel(
            mockRepoRepository,
            tokenRepository,
            backOffWork,
            detailReducerProcessor,
            standardTestDispatcherRule.testDispatcher,
            SavedStateHandle().apply {
                set("userName", userName)
                set("repoName", repoName)
            }
        )
    }

    private fun makeRepoDetailEntity() =
        RepoDetailEntity(
            id = 1,
            name = "test",
            owner = OwnerEntity(login = "test", avatarUrl = "test"),
            stargazersCount = 10,
            forksCount = 10,
            isStarred = true
        )

    private fun makeExpectedValue(isStarred: Boolean, stargazersCount: Int) =
        RepoDetailEntity(
            id = 1,
            name = "test",
            owner = OwnerEntity(login = "test", avatarUrl = "test"),
            stargazersCount = stargazersCount,
            forksCount = 10,
            isStarred = isStarred
        )
}