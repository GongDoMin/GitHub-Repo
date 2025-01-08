package com.prac.feature.main

import app.cash.turbine.test
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.model.Repository
import com.prac.data.repository.RepoRepository
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import com.prac.shared_test.common.FakeBackOffWorkManager
import com.prac.shared_test.domain.FakeClearLocalDataUseCase
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

    @Mock private lateinit var mockRepoRepository: RepoRepository
    private lateinit var clearLocalDataUseCase: FakeClearLocalDataUseCase
    private val backOffWork: FakeBackOffWorkManager = FakeBackOffWorkManager()

    private lateinit var mainActionProcessor: MainActionProcessor

    @Before
    fun setUp() {
        initialMainActionProcessor()
    }

    @Test
    fun 로딩_액션발행_이벤트없음() = runTest {
        // when, then
        mainActionProcessor(Action.InternalAction.Load).test {
            awaitComplete()
        }
    }

    @Test
    fun 스타상태요청_액션발행_이벤트없음() = runTest {
        // given
        whenever(mockRepoRepository.isStarred(fakeRepository.id, fakeRepository.name)).thenReturn(Unit)

        // when
        mainActionProcessor(Action.InternalAction.FetchStarState(fakeRepository)).test {
            awaitComplete()
        }

        // then
        verify(mockRepoRepository).isStarred(fakeRepository.id, fakeRepository.name)
    }

    @Test
    fun 로그아웃_액션발행_mutation은_ShowError() = runTest {
        mainActionProcessor(Action.InternalAction.Logout).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue((mutation as Mutation.ShowError).errorMessage == INVALID_TOKEN)
            assertTrue(event == null)
        }

        clearLocalDataUseCase.isCleared()
        assertTrue(backOffWork.getWorkSize() == 0)
    }

    @Test
    fun 레파지토리클릭_액션발행_event는_OpenRepositoryDetail() = runTest {
        // when, then
        mainActionProcessor(Action.UserAction.OnClickRepository(fakeRepository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.OpenRepositoryDetail)
        }
    }

    @Test
    fun 언스타클릭_액션발행_이벤트는없음() = runTest {
        // when
        mainActionProcessor(Action.UserAction.OnClickUnStar(fakeRepository)).test {
            awaitComplete()
        }

        // then
        verify(mockRepoRepository).starLocalRepository(fakeRepository.id, fakeRepository.stargazersCount + 1)
        verify(mockRepoRepository).starRepository(fakeRepository.owner.login, fakeRepository.name)
    }

    @Test
    fun 스타클릭_액션발행_이벤트는없음() = runTest {
        // when
        mainActionProcessor(Action.UserAction.OnClickStar(fakeRepository)).test {
            awaitComplete()
        }

        // then
        verify(mockRepoRepository).unStarLocalRepository(fakeRepository.id, fakeRepository.stargazersCount - 1)
        verify(mockRepoRepository).unStarRepository(fakeRepository.owner.login, fakeRepository.name)
    }

    @Test
    fun 언스타클릭_액션발행_IOException일때_이벤트는없음() = runTest {
        // given
        val uniqueID = "star_${fakeRepository.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.starRepository(fakeRepository.owner.login, fakeRepository.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))
        backOffWork.setScope(this)

        // when
        mainActionProcessor(Action.UserAction.OnClickUnStar(fakeRepository)).test {
            awaitComplete()
        }

        // then
        advanceUntilIdle()
        verify(mockRepoRepository).starLocalRepository(fakeRepository.id, fakeRepository.stargazersCount + 1)
        verify(mockRepoRepository, times(expectedCallTimes)).starRepository(fakeRepository.owner.login, fakeRepository.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun 스타클릭_액션발행_IOException일때_이벤트는없음() = runTest {
        // given
        val uniqueID = "star_${fakeRepository.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.unStarRepository(fakeRepository.owner.login, fakeRepository.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))
        backOffWork.setScope(this)

        // when
        mainActionProcessor(Action.UserAction.OnClickStar(fakeRepository)).test {
            awaitComplete()
        }

        // then
        advanceUntilIdle()
        verify(mockRepoRepository).unStarLocalRepository(fakeRepository.id, fakeRepository.stargazersCount - 1)
        verify(mockRepoRepository, times(expectedCallTimes)).unStarRepository(fakeRepository.owner.login, fakeRepository.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun 언스타클릭_액션발행_AuthorizationError일때_mutation은_ShowError() = runTest {
        // given
        whenever(mockRepoRepository.starRepository(fakeRepository.owner.login, fakeRepository.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        // when, then
        mainActionProcessor(Action.UserAction.OnClickUnStar(fakeRepository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun 스타클릭_액션발행_AuthorizationError일때_mutation은_ShowError() = runTest {
        // given
        whenever(mockRepoRepository.unStarRepository(fakeRepository.owner.login, fakeRepository.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        // when, then
        mainActionProcessor(Action.UserAction.OnClickStar(fakeRepository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
        }
    }

    @Test
    fun 언스타클릭_액션발행_NotFoundError일때_mutation은_ShowError() = runTest {
        // given
        whenever(mockRepoRepository.starRepository(fakeRepository.owner.login, fakeRepository.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        // when ,then
        mainActionProcessor(Action.UserAction.OnClickUnStar(fakeRepository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
            verify(mockRepoRepository).unStarLocalRepository(fakeRepository.id, fakeRepository.stargazersCount)
        }
    }

    @Test
    fun 스타클릭_액션발행_NotFoundError일때_mutation은_ShowError() = runTest {
        // given
        whenever(mockRepoRepository.unStarRepository(fakeRepository.owner.login, fakeRepository.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        // when ,then
        mainActionProcessor(Action.UserAction.OnClickStar(fakeRepository)).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowError)
            assertTrue(event == null)
            verify(mockRepoRepository).starLocalRepository(fakeRepository.id, fakeRepository.stargazersCount)
        }
    }

    @Test
    fun 재시도_액션발행_event는_Retry() = runTest {
        // when, then
        mainActionProcessor(Action.UserAction.OnClickRetry).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Retry)
        }
    }

    @Test
    fun 다이어로그해제_액션발행_mutation는_ShowContent() = runTest {
        mainActionProcessor(Action.UserAction.DialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowContent)
            assertTrue(event == null)
        }
    }

    @Test
    fun 로그아웃다이어로그해제_액션발행_event는_Logout() = runTest {
        mainActionProcessor(Action.UserAction.LogoutDialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Logout)
        }
    }

    private fun initialMainActionProcessor() {
        clearLocalDataUseCase = FakeClearLocalDataUseCase(
            userName = "son",
            accessToken = "token",
            list = listOf("first", "second")
        )
        mainActionProcessor = MainActionProcessor(
            repoRepository = mockRepoRepository,
            clearLocalDataUseCase = clearLocalDataUseCase,
            backOffWorkManager = backOffWork
        )
    }

    companion object {
        private val fakeRepository = Repository()
    }
}