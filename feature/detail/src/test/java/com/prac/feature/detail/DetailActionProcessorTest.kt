package com.prac.feature.detail

import app.cash.turbine.test
import com.prac.data.exception.CommonException
import com.prac.data.model.Owner
import com.prac.data.model.RepositoryDetail
import com.prac.data.repository.RepoRepository
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event
import com.prac.feature.detail.model.Mutation
import com.prac.shared_test.common.FakeBackOffWorkManager
import com.prac.shared_test.domain.FakeClearLocalDataUseCase
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
    private val backOffWork: FakeBackOffWorkManager = FakeBackOffWorkManager()

    private lateinit var clearLocalDataUseCase: FakeClearLocalDataUseCase
    private lateinit var detailActionProcessor: DetailActionProcessor

    @Before
    fun setUp() {
        initialDetailActionProcessor()
    }

    @Test
    fun 레파지토리_액션발행_mutation은_ShowRepository() = runTest {
        // given
        val starStateAndCount = Pair(true, 11)
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(fakeRepository))
        whenever(mockRepoRepository.getStarStateAndStarCount(fakeRepository.id))
            .thenReturn(flow { emit(starStateAndCount) })

        // when, then
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
    fun 레파지토리_액션발행_Input이_유효하지않을때_mutation은_ShowError() = runTest {
        // when, then
        detailActionProcessor(Action.InternalAction.GetRepository(null, null)).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun 레파지토리_액션발행_NetworkError일때_mutation은_ShowError() = runTest {
        // given
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        // when, then
        detailActionProcessor(Action.InternalAction.GetRepository(userName, repoName)).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun 레파지토리_액션발행_AuthorizationError일때_mutation은_ShowError() = runTest {
        // given
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        // when, then
        detailActionProcessor(Action.InternalAction.GetRepository(userName, repoName)).test {
            awaitItem() // loadingState

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun 언스타클릭_액션발행() = runTest {
        // given
        whenever(mockRepoRepository.starRepository(userName, repoName))
            .thenReturn(Result.success(Unit))

        // when
        detailActionProcessor(Action.UserAction.OnClickUnStar(fakeRepository)).test {
            awaitComplete()
        }

        // then
        advanceUntilIdle()
        verify(mockRepoRepository).starLocalRepository(fakeRepository.id, fakeRepository.stargazersCount + 1)
        verify(mockRepoRepository).starRepository(userName, repoName)
    }

    @Test
    fun 스타클릭_액션발행() = runTest {
        // given
        whenever(mockRepoRepository.unStarRepository(userName, repoName))
            .thenReturn(Result.success(Unit))

        // when
        detailActionProcessor(Action.UserAction.OnClickStar(fakeRepository)).test {
            awaitComplete()
        }

        // then
        advanceUntilIdle()
        verify(mockRepoRepository).unStarLocalRepository(fakeRepository.id, fakeRepository.stargazersCount - 1)
        verify(mockRepoRepository).unStarRepository(userName, repoName)
    }

    @Test
    fun 언스타클릭_액션발행_IOException일때_이벤트는없음() = runTest {
        // given
        backOffWork.setScope(this)
        val uniqueID = "star_${fakeRepository.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.starRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        // when
        detailActionProcessor(Action.UserAction.OnClickUnStar(fakeRepository)).test {
            awaitComplete()
        }

        // then
        advanceUntilIdle()
        verify(mockRepoRepository).starLocalRepository(fakeRepository.id, fakeRepository.stargazersCount + 1)
        verify(mockRepoRepository, times(expectedCallTimes)).starRepository(userName, repoName)
        Assert.assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun 스타클릭_액션발행_IOException일때_이벤트는없음() = runTest {
        // given
        backOffWork.setScope(this)
        val uniqueID = "star_${fakeRepository.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.unStarRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        // when
        detailActionProcessor(Action.UserAction.OnClickStar(fakeRepository)).test {
            awaitComplete()
        }

        // then
        advanceUntilIdle()
        verify(mockRepoRepository).unStarLocalRepository(fakeRepository.id, fakeRepository.stargazersCount - 1)
        verify(mockRepoRepository, times(expectedCallTimes)).unStarRepository(userName, repoName)
        Assert.assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun 다이어로그해제_액션발행_event는_Error() = runTest {
        // when, then
        detailActionProcessor(Action.UserAction.DialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Error)
        }
    }

    @Test
    fun 로그아웃_액션발행_event는_Logout() = runTest {
        // when, then
        detailActionProcessor(Action.UserAction.LogoutDialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Logout)
        }
    }

    private fun initialDetailActionProcessor() {
        clearLocalDataUseCase = FakeClearLocalDataUseCase(
            userName = "son",
            accessToken = "token",
            list = listOf("list1", "list2")
        )
        detailActionProcessor = DetailActionProcessor(
            repoRepository = mockRepoRepository,
            clearLocalDataUseCase = clearLocalDataUseCase,
            backOffWorkManager = backOffWork
        )
    }

    companion object {
        private val fakeRepository = RepositoryDetail()
        private val userName = fakeRepository.owner.login
        private val repoName = fakeRepository.name
    }
}