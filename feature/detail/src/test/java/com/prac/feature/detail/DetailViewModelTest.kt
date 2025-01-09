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
import com.prac.data.model.RepositoryDetail
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.view.UiState
import com.prac.shared_test.rules.StandardTestDispatcherRule
import com.prac.shared_test.ui.FakeDetailActionProcessor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

    @Test
    fun 레파지토리_액션발행_uiState는_Content() = runTest {
        // given
        initialViewModel(userName, repoName)

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState

            val result = awaitItem()
            assertEquals((result as UiState.Content).repository.owner.login, userName)
            assertEquals(result.repository.name, repoName)
        }
    }

    @Test
    fun 레파지토리_액션발행_Input이_유효하지않을때_uiState는_Error() = runTest {
        // given
        initialViewModel(null, null)

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertTrue((result as UiState.Error).message.isNotEmpty())
        }
    }

    @Test
    fun 레파지토리_액션발행_NetworkError일때_uiState는_Error() = runTest {
        // given
        initialViewModel(userName, repoName, CommonException.NetworkError())

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, CONNECTION_FAIL)
        }
    }

    @Test
    fun 레파지토리_액션발행_AuthorizationError일때_uiState는_Error() = runTest {
        //given
        initialViewModel(userName, repoName, CommonException.AuthorizationError())

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun 언스타클릭_액션발행_AuthorizationError일때_uiState는_Error() = runTest {
        // given
        initialViewModel(userName, repoName)

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(CommonException.AuthorizationError())
            detailViewModel.process(Action.UserAction.OnClickUnStar(fakeRepository))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun 스타클릭_액션발행_AuthorizationError일때_uiState는_Error() = runTest {
        // given
        initialViewModel(userName, repoName)

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(CommonException.AuthorizationError())
            detailViewModel.process(Action.UserAction.OnClickStar(fakeRepository))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_TOKEN)
        }
    }

    @Test
    fun 언스타클릭_액션발행_NotFoundRepositoryError일때_uiState는_Error() = runTest {
        // given
        initialViewModel(userName, repoName)

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(RepositoryException.NotFoundRepository())
            detailViewModel.process(Action.UserAction.OnClickUnStar(fakeRepository))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_REPOSITORY)
        }
    }

    @Test
    fun 스타클릭_액션발행_NotFoundRepositoryError일때_uiState는_Error() = runTest {
        // given
        initialViewModel(userName, repoName)

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(RepositoryException.NotFoundRepository())
            detailViewModel.process(Action.UserAction.OnClickStar(fakeRepository))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, INVALID_REPOSITORY)
        }
    }

    @Test
    fun 언스타클릭_액션발행_UnKnownError일때_uiState는_Error() = runTest {
        // given
        initialViewModel(userName, repoName)

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(CommonException.UnKnownError())
            detailViewModel.process(Action.UserAction.OnClickUnStar(fakeRepository))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, UNKNOWN)
        }
    }

    @Test
    fun 스타클릭_액션발행_UnKnownError일때_uiState는_Error() = runTest {
        // given
        initialViewModel(userName, repoName)

        // when, then
        detailViewModel.uiStateFlow.test {
            awaitItem() // loadingState
            awaitItem() // showRepository

            detailActionProcessorSetThrowable(CommonException.UnKnownError())
            detailViewModel.process(Action.UserAction.OnClickStar(fakeRepository))

            val result = awaitItem()
            assertTrue(result is UiState.Error)
            assertEquals((result as UiState.Error).message, UNKNOWN)
        }
    }

    @Test
    fun 다이어로그해제_액션발행_event는_Error() = runTest {
        // given
        initialViewModel(userName, repoName)

        // when
        detailViewModel.process(Action.UserAction.DialogDismiss)

        // then
        detailViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.Error)
        }
    }

    @Test
    fun 로그아웃다이어로그해제_액션발행_event는_Logout() = runTest {
        // given
        initialViewModel(userName, repoName)

        // when
        detailViewModel.process(Action.UserAction.LogoutDialogDismiss)

        // then
        detailViewModel.eventFlow.test {
            val result = awaitItem()
            assertTrue(result is Event.Logout)
        }
    }

    private fun initialViewModel(
        userName: String?,
        repoName: String?,
        throwable: Throwable? = null
    ) {
        detailActionProcessor = FakeDetailActionProcessor(throwable)

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

    companion object {
        private val fakeRepository = RepositoryDetail()
        private val userName = fakeRepository.owner.login
        private val repoName = fakeRepository.name
    }
}
