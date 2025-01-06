package com.prac.feature.detail

import app.cash.turbine.test
import com.prac.data.exception.CommonException
import com.prac.data.model.OwnerModel
import com.prac.data.model.RepoDetailModel
import com.prac.data.repository.RepoRepository
import com.prac.domain.ClearLocalDataUseCase
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.model.toRepositoryDetail
import com.prac.shared_test.common.FakeBackOffWorkManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
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
class DetailActionProcessorTest {

    @Mock private lateinit var mockRepoRepository: RepoRepository
    @Mock private lateinit var mockClearLocalDataUseCase: ClearLocalDataUseCase
    private val backOffWork: FakeBackOffWorkManager = FakeBackOffWorkManager()
    private lateinit var detailActionProcessor: DetailActionProcessor

    private val repoDetailEntity =
        RepoDetailModel(
            id = 1,
            name = "test",
            owner = OwnerModel(login = "test"),
            stargazersCount = 10,
            isStarred = true,
        )
    private val userName = repoDetailEntity.owner.login
    private val repoName = repoDetailEntity.name

    @Before
    fun setUp() {
        detailActionProcessor = DetailActionProcessor(
            repoRepository = mockRepoRepository,
            clearLocalDataUseCase = mockClearLocalDataUseCase,
            backOffWorkManager = backOffWork
        )
    }

    @Test
    fun invoke_actionIsGetRepository_mutationIsShowRepository() = runTest {
        val starStateAndCount = Pair(true, 11)
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(mockRepoRepository.getStarStateAndStarCount(repoDetailEntity.id))
            .thenReturn(flow { emit(starStateAndCount) })

        detailActionProcessor(Action.InternalAction.GetRepository(userName, repoName)).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowRepository)
            assertTrue((mutation as Mutation.ShowRepository).repository.isStarred == true)
            assertTrue(mutation.repository.stargazersCount == 11)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsGetRepository_mutationIsError_whenInvalidInput() = runTest {
        detailActionProcessor(Action.InternalAction.GetRepository(null, null)).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsGetRepository_mutationIsError_whenNetworkError() = runTest {
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        detailActionProcessor(Action.InternalAction.GetRepository(userName, repoName)).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsGetRepository_mutationIsError_whenAuthorizationError() = runTest {
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        detailActionProcessor(Action.InternalAction.GetRepository(userName, repoName)).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun invoke_actionIsOnClickUnStar_emitNothing() = runTest {
        whenever(mockRepoRepository.starRepository(userName, repoName))
            .thenReturn(Result.success(Unit))

        detailActionProcessor(Action.UserAction.OnClickUnStar(repoDetailEntity.toRepositoryDetail())).test {
            awaitComplete()
        }
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount + 1)
        verify(mockRepoRepository).starRepository(userName, repoName)
    }

    @Test
    fun invoke_actionIsOnClickStar_emitNothing() = runTest {
        whenever(mockRepoRepository.unStarRepository(userName, repoName))
            .thenReturn(Result.success(Unit))

        detailActionProcessor(Action.UserAction.OnClickStar(repoDetailEntity.toRepositoryDetail())).test {
            awaitComplete()
        }
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount - 1)
        verify(mockRepoRepository).unStarRepository(userName, repoName)
    }

    @Test
    fun invoke_actionIsOnClickUnStar_emitNothing_whenNetworkError() = runTest {
        backOffWork.setScope(this)
        val repoModel = RepoDetailModel()
        val uniqueID = "star_${repoModel.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.starRepository(repoModel.owner.login, repoModel.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        detailActionProcessor(Action.UserAction.OnClickUnStar(repoModel.toRepositoryDetail())).test {
            awaitComplete()
        }

        advanceUntilIdle()
        verify(mockRepoRepository).starLocalRepository(repoModel.id, repoModel.stargazersCount + 1)
        verify(mockRepoRepository, times(expectedCallTimes)).starRepository(repoModel.owner.login, repoModel.name)
        Assert.assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun invoke_actionIsOnClickStar_emitNothing_whenNetworkError() = runTest {
        backOffWork.setScope(this)
        val repoModel = RepoDetailModel()
        val uniqueID = "star_${repoModel.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.unStarRepository(repoModel.owner.login, repoModel.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        detailActionProcessor(Action.UserAction.OnClickStar(repoModel.toRepositoryDetail())).test {
            awaitComplete()
        }

        advanceUntilIdle()
        verify(mockRepoRepository).unStarLocalRepository(repoModel.id, repoModel.stargazersCount - 1)
        verify(mockRepoRepository, times(expectedCallTimes)).unStarRepository(repoModel.owner.login, repoModel.name)
        Assert.assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun invoke_actionIsOnClickUnStar_mutationIsError_whenAuthorizationError() = runTest {
        whenever(mockRepoRepository.starRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        detailActionProcessor(Action.UserAction.OnClickUnStar(repoDetailEntity.toRepositoryDetail())).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }

        assertTrue(backOffWork.getWorkSize() == 0)
        verify(mockRepoRepository).clearRepositories()
        verify(mockClearLocalDataUseCase).invoke()
    }

    @Test
    fun invoke_actionIsOnClickStar_mutationIsError_whenAuthorizationError() = runTest {
        whenever(mockRepoRepository.unStarRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        detailActionProcessor(Action.UserAction.OnClickStar(repoDetailEntity.toRepositoryDetail())).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }

        assertTrue(backOffWork.getWorkSize() == 0)
        verify(mockRepoRepository).clearRepositories()
        verify(mockClearLocalDataUseCase).invoke()
    }

    @Test
    fun invoke_actionIsDialogDismiss_eventIsError() = runTest {
        detailActionProcessor(Action.UserAction.DialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Error)
        }
    }

    @Test
    fun invoke_actionIsLogout_eventIsLogout() = runTest {
        detailActionProcessor(Action.UserAction.LogoutDialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Logout)
        }
    }
}