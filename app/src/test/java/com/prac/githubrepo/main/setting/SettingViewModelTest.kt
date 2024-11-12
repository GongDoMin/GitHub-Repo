package com.prac.githubrepo.main.setting

import com.prac.data.repository.RepoRepository
import com.prac.githubrepo.ui.home.main.setting.SettingViewModel
import com.prac.githubrepo.util.FakeBackOffWorkManager
import com.prac.githubrepo.util.StandardTestDispatcherRule
import com.prac.shared_test.data.FakeTokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SettingViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private lateinit var tokenRepository: FakeTokenRepository
    @Mock private lateinit var mockRepoRepository: RepoRepository
    private lateinit var backOffWorkManager: FakeBackOffWorkManager

    private lateinit var settingViewModel: SettingViewModel

    private val token = "test"

    @Before
    fun setup() {
        tokenRepository = FakeTokenRepository(token)
        backOffWorkManager = FakeBackOffWorkManager()

        settingViewModel = SettingViewModel(tokenRepository, mockRepoRepository, standardTestDispatcherRule.testDispatcher, backOffWorkManager)
    }

    @Test
    fun logout_success_eventIsSuccessAndTokenIsRemoved() = runTest {

        settingViewModel.logout()

        val event = settingViewModel.event.first()

        assert(event is SettingViewModel.Event.Success)
        assertFalse(tokenRepository.isLoggedIn())
    }
}