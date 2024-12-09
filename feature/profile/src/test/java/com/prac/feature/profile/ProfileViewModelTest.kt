package com.prac.feature.profile

import app.cash.turbine.test
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event
import com.prac.feature.profile.model.Mutation
import com.prac.feature.profile.view.UiState
import com.prac.shared_test.rules.StandardTestDispatcherRule
import com.prac.shared_test.ui.FakeProfileActionProcessor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private val profileReducerProcessor: Reducer<Mutation, UiState> = ProfileReducerProcessor()
    private val profileActionProcessor: ActionProcessor<Action, Mutation, Event> = FakeProfileActionProcessor()

    private val profileViewModel: ProfileViewModel = ProfileViewModel(
        profileReducerProcessor = profileReducerProcessor,
        profileActionProcessor = profileActionProcessor,
        ioDispatcher = standardTestDispatcherRule.testDispatcher,
    )

    @Test
    fun process_actionIsOnClickLogoutButton_uiStateIsDialog() = runTest {
        profileViewModel.uiStateFlow.test {
            awaitItem() // initialState

            profileViewModel.process(Action.UserAction.OnClickLogoutButton)

            val result = awaitItem()
            assertTrue(result.isDialog)
        }
    }

    @Test
    fun process_actionIsDialogDismiss_uiStateIsIdle() = runTest {
        profileViewModel.uiStateFlow.test {
            awaitItem() // initialState

            profileViewModel.process(Action.UserAction.OnClickLogoutButton)
            awaitItem() // isDialog

            profileViewModel.process(Action.UserAction.DialogDismiss)

            val result = awaitItem()
            assertFalse(result.isLoading)
            assertFalse(result.isDialog)
        }
    }

    @Test
    fun process_actionIsOnClickNegativeButton_uiStateIsIdle() = runTest {
        profileViewModel.uiStateFlow.test {
            awaitItem() // initialState

            profileViewModel.process(Action.UserAction.OnClickLogoutButton)
            awaitItem() // isDialog

            profileViewModel.process(Action.UserAction.OnClickNegativeButton)

            val result = awaitItem()
            assertFalse(result.isLoading)
            assertFalse(result.isDialog)
        }
    }

    @Test
    fun process_actionIsOnClickPositiveButton_EventIsLogout() = runTest {
        profileViewModel.eventFlow.test {
            profileViewModel.process(Action.UserAction.OnClickPositiveButton)

            val result = awaitItem()
            assertEquals(result, Event.Logout)
        }
    }
}