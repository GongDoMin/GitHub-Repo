package com.prac.feature.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.constants.UNKNOWN
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.navigation.Routes.HOME.DETAIL.Companion.REPO_NAME
import com.prac.core.navigation.Routes.HOME.DETAIL.Companion.USER_NAME
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.model.OwnerModel
import com.prac.data.model.RepoDetailModel
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.view.UiState
import com.prac.shared_test.rules.StandardTestDispatcherRule
import com.prac.shared_test.ui.FakeDetailActionProcessor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private val detailReducerProcessor = DetailReducerProcessor()
    private lateinit var detailActionProcessor: ActionProcessor<Action, Mutation, Event>

    private lateinit var detailViewModel: DetailViewModel

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

    @Test
    fun process_actionIsGetRepository_uiStateHasRepository_whenValidInput() = runTest {
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState

            val result = awaitItem()
            assertEquals((result as UiState.Content).repository.owner.login, userName)
            assertEquals(result.repository.name, repoName)
        }
    }

    @Test
    fun process_actionIsGetRepository_uiStateIsError_whenInvalidInput() = runTest {
        initViewModel(null, null)

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertTrue((result as UiState.Error).message.isNotEmpty())
        }
    }

    @Test
    fun process_actionIsGetRepository_uiStateIsError_whenNetworkError() = runTest {
        detailActionProcessor = FakeDetailActionProcessor().apply {
            setThrowable(CommonException.NetworkError())
        }
        detailViewModel = DetailViewModel(
            detailReducerProcessor = detailReducerProcessor,
            detailActionProcessor = detailActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher,
            savedStateHandle = SavedStateHandle().apply {
                set(USER_NAME, userName)
                set(REPO_NAME, repoName)
            }
        )

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, CONNECTION_FAIL)
        }
    }

    @Test
    fun process_actionIsGetRepository_uiStateIsError_whenAuthorizationError() = runTest {
        detailActionProcessor = FakeDetailActionProcessor().apply {
            setThrowable(CommonException.AuthorizationError())
        }
        detailViewModel = DetailViewModel(
            detailReducerProcessor = detailReducerProcessor,
            detailActionProcessor = detailActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher,
            savedStateHandle = SavedStateHandle().apply {
                set(USER_NAME, userName)
                set(REPO_NAME, repoName)
            }
        )

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun process_actionIsOnClickUnStar_uiStateHasDialogMessage_whenAuthorizationError() = runTest {
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(CommonException.AuthorizationError())
            detailViewModel.process(Action.UserAction.OnClickUnStar(repoDetailEntity))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun process_actionIsOnClickStar_uiStateHasDialogMessage_whenAuthorizationError() = runTest {
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(CommonException.AuthorizationError())
            detailViewModel.process(Action.UserAction.OnClickStar(repoDetailEntity))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun process_actionIsOnClickUnStar_uiStateHasDialogMessage_whenNotFoundRepository() = runTest {
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(RepositoryException.NotFoundRepository())
            detailViewModel.process(Action.UserAction.OnClickUnStar(repoDetailEntity))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_REPOSITORY)
        }
    }

    @Test
    fun process_actionIsOnClickStar_uiStateHasDialogMessage_whenNotFoundRepository() = runTest {
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(RepositoryException.NotFoundRepository())
            detailViewModel.process(Action.UserAction.OnClickStar(repoDetailEntity))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_REPOSITORY)
        }
    }

    @Test
    fun process_actionIsOnClickUnStar_uiStateHasDialogMessage_whenNotUnKnownError() = runTest {
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(CommonException.UnKnownError())
            detailViewModel.process(Action.UserAction.OnClickUnStar(repoDetailEntity))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, UNKNOWN)
        }
    }

    @Test
    fun process_actionIsOnClickStar_uiStateHasDialogMessage_whenNotUnKnownError() = runTest {
        initViewModel(userName, repoName)

        detailViewModel.uiStateFlow.test {
            awaitItem() // initialState
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(CommonException.UnKnownError())
            detailViewModel.process(Action.UserAction.OnClickStar(repoDetailEntity))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, UNKNOWN)
        }
    }

    @Test
    fun process_actionIsDialogDismiss_eventIsError() = runTest {
        initViewModel(null, null)

        detailViewModel.process(Action.UserAction.DialogDismiss)

        detailViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.Error)
        }
    }

    @Test
    fun process_actionIsLogoutDialogDismiss_eventIsLogout() = runTest {
        initViewModel(null, null)

        detailViewModel.process(Action.UserAction.LogoutDialogDismiss)

        detailViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.Logout)
        }
    }

    private fun initViewModel(userName: String?, repoName: String?) {
        detailActionProcessor = FakeDetailActionProcessor()
        detailViewModel = DetailViewModel(
            detailReducerProcessor = detailReducerProcessor,
            detailActionProcessor = detailActionProcessor,
            ioDispatcher = standardTestDispatcherRule.testDispatcher,
            savedStateHandle = SavedStateHandle().apply {
                set(USER_NAME, userName)
                set(REPO_NAME, repoName)
            }
        )
    }

    private fun detailActionProcessorSetThrowable(throwable: Throwable) {
        (detailActionProcessor as FakeDetailActionProcessor).setThrowable(throwable)
    }
}
