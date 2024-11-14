package com.prac.githubrepo.profile

import com.prac.data.repository.RepoRepository
import com.prac.githubrepo.ui.profile.ProfileViewModel
import com.prac.githubrepo.util.FakeBackOffWorkManager
import com.prac.githubrepo.util.StandardTestDispatcherRule
import com.prac.shared_test.data.FakeTokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
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

    private lateinit var tokenRepository: FakeTokenRepository
    @Mock
    private lateinit var mockRepoRepository: RepoRepository
    private lateinit var backOffWorkManager: FakeBackOffWorkManager

    private lateinit var profileViewModel: ProfileViewModel

    private val token = "test"

    @Before
    fun setup() {
        tokenRepository = FakeTokenRepository(token)
        backOffWorkManager = FakeBackOffWorkManager()

        profileViewModel = ProfileViewModel(tokenRepository, mockRepoRepository, standardTestDispatcherRule.testDispatcher, backOffWorkManager)
    }

    @Test
    fun logout_success_eventIsSuccessAndTokenIsRemoved() = runTest {

        profileViewModel.logout()

        val event = profileViewModel.event.first()

        assert(event is ProfileViewModel.Event.Success)
        Assert.assertFalse(tokenRepository.isLoggedIn())
    }
}