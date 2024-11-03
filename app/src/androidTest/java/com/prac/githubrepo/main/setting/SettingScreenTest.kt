package com.prac.githubrepo.main.setting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.prac.githubrepo.MainActivity
import com.prac.githubrepo.R
import com.prac.githubrepo.util.hasButton
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SettingScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val activity get() = composeTestRule.activity

    private var isMainScreen = false

    @Before
    fun setup() {
        hiltRule.inject()
        setContent()
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
                onLogout = { isMainScreen = true }
            )
        }
    }
}