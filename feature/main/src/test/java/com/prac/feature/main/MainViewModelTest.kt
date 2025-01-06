package com.prac.feature.main

import app.cash.turbine.test
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.domain.GetRepositoriesUseCase
import com.prac.domain.entity.RepoEntity
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.model.Repository
import com.prac.feature.main.view.UiState
import com.prac.shared_test.domain.FakeGetRepositoriesUseCase
import com.prac.shared_test.rules.StandardTestDispatcherRule
import com.prac.shared_test.ui.FakeMainActionProcessor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private val mainReducerProcessor = MainReducerProcessor()

    private val getRepositoriesUseCase: GetRepositoriesUseCase = FakeGetRepositoriesUseCase(listOf(RepoEntity(stargazersCount = 1)))
    private lateinit var mainActionProcessor: ActionProcessor<Action, Mutation, Event>
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() = runTest {
        mainActionProcessor = FakeMainActionProcessor()

        mainViewModel = MainViewModel(
            getRepositoriesUseCase = getRepositoriesUseCase,
            mainReducerProcessor = mainReducerProcessor,
            mainActionProcessor = mainActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher
        )
    }

    @Test
    fun process_actionIsOnClickUnStar_uiStateIsError_whenAuthorizationError() = runTest {
        mainActionProcessorSetThrowable(CommonException.AuthorizationError())

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickUnStar(Repository()))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun process_actionIsOnClickStar_uiStateIsError_whenAuthorizationError() = runTest {
        mainActionProcessorSetThrowable(CommonException.AuthorizationError())

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickStar(Repository()))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun process_actionIsOnClickUnStar_uiStateIsError_whenNotFoundRepository() = runTest {
        mainActionProcessorSetThrowable(RepositoryException.NotFoundRepository())

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickUnStar(Repository()))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_REPOSITORY)
        }
    }

    @Test
    fun process_actionIsOnClickStar_uiStateIsError_whenNotFoundRepository() = runTest {
        mainActionProcessorSetThrowable(RepositoryException.NotFoundRepository())

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickStar(Repository()))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_REPOSITORY)
        }
    }

    @Test
    fun process_actionIsOnClickUnStar_uiStateIsError_whenUnKnownError() = runTest {
        mainActionProcessorSetThrowable(CommonException.UnKnownError())

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickUnStar(Repository()))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, UNKNOWN)
        }
    }

    @Test
    fun process_actionIsOnClickStar_uiStateIsError_whenUnKnownError() = runTest {
        mainActionProcessorSetThrowable(CommonException.UnKnownError())

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickStar(Repository()))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, UNKNOWN)
        }
    }

    @Test
    fun process_actionIsLogout_uiStateIsError() = runTest {
        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState
            mainViewModel.process(Action.InternalAction.Logout)

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun process_actionIsDialogDismiss_uiStateIsIdle() = runTest {
        mainActionProcessorSetThrowable(CommonException.UnKnownError())

        mainViewModel.uiStateFlow.test {
            awaitItem() // initialState

            mainViewModel.process(Action.UserAction.OnClickStar(Repository()))
            awaitItem() // showError

            mainViewModel.process(Action.UserAction.DialogDismiss)

            val result = awaitItem()
            assertFalse(result is UiState.Error)
        }
    }

    @Test
    fun process_actionIsLogoutDialogDismiss_eventIsLogout() = runTest {
        mainViewModel.eventFlow.test {
            mainViewModel.process(Action.UserAction.LogoutDialogDismiss)

            val result = awaitItem()
            assertTrue(result is Event.Logout)
        }
    }

    @Test
    fun process_actionIsOnClickRepository_eventIsOpenRepository() = runTest {
        mainViewModel.eventFlow.test {
            mainViewModel.process(Action.UserAction.OnClickRepository(Repository()))

            val result = awaitItem()
            assertTrue(result is Event.OpenRepositoryDetail)
        }
    }

    @Test
    fun process_actionIsOnClickRetry_eventIsRetry() = runTest {
        mainViewModel.eventFlow.test {
            mainViewModel.process(Action.UserAction.OnClickRetry)

            val result = awaitItem()
            assertTrue(result is Event.Retry)
        }
    }

    private fun mainActionProcessorSetThrowable(throwable: Throwable) {
        (mainActionProcessor as FakeMainActionProcessor).setThrowable(throwable)
    }
}