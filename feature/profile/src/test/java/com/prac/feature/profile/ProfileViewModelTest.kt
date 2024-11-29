package com.prac.feature.profile

import app.cash.turbine.test
import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event
import com.prac.feature.profile.model.Mutation
import com.prac.feature.profile.view.UiState
import com.prac.shared_test.data.FakeTokenRepository
import com.prac.shared_test.rules.StandardTestDispatcherRule
import com.prac.shared_test.ui.FakeBackOffWorkManager
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProfileViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private val tokenRepository: TokenRepository = FakeTokenRepository("test")
    @Mock private lateinit var mockRepoRepository: RepoRepository
    private val profileReducerProcessor: Reducer<Mutation, UiState> = ProfileReducerProcessor()
    private val backOffWorkManager: BackOffWorkManager = FakeBackOffWorkManager()

    private lateinit var profileViewModel: ProfileViewModel

    @Before
    fun setup() {
        profileViewModel = ProfileViewModel(
            tokenRepository,
            mockRepoRepository,
            profileReducerProcessor,
            standardTestDispatcherRule.testDispatcher,
            backOffWorkManager
        )
    }

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
            awaitItem() // isDialog = true

            profileViewModel.process(Action.UserAction.DialogDismiss)

            val result = awaitItem()
            assertFalse(result.isLoading)
            assertFalse(result.isDialog)
        }
    }

    @Test
    fun process_actionIsOnClickCheckButton_EventIsLogout() = runTest {
        profileViewModel.eventFlow.test {
            profileViewModel.process(Action.UserAction.OnClickCheckButton)

            val result = awaitItem()
            assertEquals(result, Event.Logout)
        }
    }
}