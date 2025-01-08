package com.prac.feature.main

import app.cash.turbine.test
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.data.model.Repository
import com.prac.domain.GetRepositoriesUseCase
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.view.UiState
import com.prac.shared_test.domain.FakeGetRepositoriesUseCase
import com.prac.shared_test.rules.StandardTestDispatcherRule
import com.prac.shared_test.ui.FakeMainActionProcessor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private val mainReducerProcessor = MainReducerProcessor()
    private val getRepositoriesUseCase: GetRepositoriesUseCase = FakeGetRepositoriesUseCase(listOf(Repository(stargazersCount = 1)))

    private lateinit var mainActionProcessor: ActionProcessor<Action, Mutation, Event>
    private lateinit var mainViewModel: MainViewModel

    @Test
    fun 언스타클릭_액션발행_uiState는_Error() = runTest {
        // given
        initialMainViewModel(CommonException.AuthorizationError())

        // when, then
        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickUnStar(Repository()))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun 스타클릭_액션발행_uiState는_Error() = runTest {
        // given
        initialMainViewModel(CommonException.AuthorizationError())

        // when, then
        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickStar(Repository()))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun 로그아웃_액션발행_uiState는_Error() = runTest {
        // given
        initialMainViewModel()

        // when, then
        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState
            mainViewModel.process(Action.InternalAction.Logout)

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun 다이어로그해제_액션발행_uiState는_Content() = runTest {
        initialMainViewModel(CommonException.UnKnownError())

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickStar(Repository()))
            awaitItem() // showError

            mainViewModel.process(Action.UserAction.DialogDismiss)

            val result = awaitItem()
            assertTrue(result is UiState.Content)
        }
    }

    @Test
    fun 로그아웃다이어로그해제_액션발행_event는_Logout() = runTest {
        // given
        initialMainViewModel()

        // when, then
        mainViewModel.eventFlow.test {
            mainViewModel.process(Action.UserAction.LogoutDialogDismiss)

            val result = awaitItem()
            assertTrue(result is Event.Logout)
        }
    }

    @Test
    fun 레파지토리클릭_액션발행_event는_OpenRepositoryDetail() = runTest {
        // given
        initialMainViewModel()

        // when, then
        mainViewModel.eventFlow.test {
            mainViewModel.process(Action.UserAction.OnClickRepository(Repository()))

            val result = awaitItem()
            assertTrue(result is Event.OpenRepositoryDetail)
        }
    }

    @Test
    fun 재시도클릭_액션발행_event는_Retry() = runTest {
        // given
        initialMainViewModel()

        // when, then
        mainViewModel.eventFlow.test {
            mainViewModel.process(Action.UserAction.OnClickRetry)

            val result = awaitItem()
            assertTrue(result is Event.Retry)
        }
    }

    private fun initialMainViewModel(
        throwable: Throwable? = null
    ) {
        mainActionProcessor = FakeMainActionProcessor(throwable)

        mainViewModel = MainViewModel(
            getRepositoriesUseCase = getRepositoriesUseCase,
            mainReducerProcessor = mainReducerProcessor,
            mainActionProcessor = mainActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher
        )
    }
}