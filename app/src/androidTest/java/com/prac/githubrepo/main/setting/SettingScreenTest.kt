package com.prac.githubrepo.main.setting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.MainActivity
import com.prac.githubrepo.R
import com.prac.githubrepo.util.BackOffWorkManager
import com.prac.githubrepo.util.hasButton
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SettingScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val activity get() = composeTestRule.activity

    private var isMainScreen = false

    @Inject
    lateinit var tokenRepository: TokenRepository
    @Inject
    lateinit var repoRepository: RepoRepository

    @Before
    fun setup() {
        hiltRule.inject()
        setContent()
    }

    @Test
    fun displaySettingScreen_whenUiStateIsIdle() {
        composeTestRule.onNode(hasButton(R.string.logout)).assertIsDisplayed()
        composeTestRule.onNodeWithText(activity.getString(R.string.logout)).assertIsDisplayed()
    }

    @Test
    fun loginButtonClick_showLogoutAlertDialog() = runTest {
        composeTestRule.onNode(hasButton(R.string.logout)).performClick()

        composeTestRule.onNodeWithText(activity.getString(R.string.logout_confirm)).assertIsDisplayed()
    }

    @Test
    fun loginButtonClick_showLogoutAlertDialog_cancelButtonClick() = runTest {
        composeTestRule.onNode(hasButton(R.string.logout)).performClick()

        composeTestRule.onNodeWithText(activity.getString(R.string.logout_confirm)).assertIsDisplayed()

        composeTestRule.onNodeWithText(activity.getString(R.string.cancel)).performClick()

        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(activity.getString(R.string.logout_confirm)).isNotDisplayed() && !isMainScreen
        }
    }

    @Test
    fun loginButtonClick_showLogoutAlertDialog_checkButtonClick() = runTest {
        composeTestRule.onNode(hasButton(R.string.logout)).performClick()

        composeTestRule.onNodeWithText(activity.getString(R.string.logout_confirm)).assertIsDisplayed()

        composeTestRule.onNodeWithText(activity.getString(R.string.check)).performClick()

        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(activity.getString(R.string.logout_confirm)).isNotDisplayed() && isMainScreen
        }
    }

    private fun setContent() {
        composeTestRule.setContent {
            SettingScreen(
                viewModel = SettingViewModel(tokenRepository, repoRepository, Dispatchers.IO, FakeBackOffWorkManager()),
                onLogout = { isMainScreen = true }
            )
        }
    }

    private class FakeBackOffWorkManager: BackOffWorkManager {
        override fun addWork(uniqueID: String, times: Int, initialDelay: Long, maxDelay: Long, factor: Double, work: suspend () -> Result<*>) { }

        override fun clearWork() { }
    }
}