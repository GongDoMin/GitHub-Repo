package com.prac.githubrepo.login

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import com.prac.githubrepo.BuildConfig
import com.prac.githubrepo.HiltTestActivity
import com.prac.githubrepo.R
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.LOGIN_FAIL
import com.prac.feature.login.view.LoginScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    private var isMainScreen: Boolean = false

    @Before
    fun setUp() {
        hiltRule.inject()
        Intents.init()
        setContent()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun loginButtonClick_openBrowser() = runTest {
        composeTestRule.onNodeWithText(activity.getString(R.string.login)).performClick()

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse(BuildConfig.GITHUB_OAUTH_URI)))
    }

    @Test
    fun onNewIntent_validIntent_navigateToMainActivity() = runTest {
        val scheme = "test"
        val host = "test"
        val code = "success"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
        activity.startActivity(intent)

        composeTestRule.waitUntil {
            isMainScreen
        }
    }

    @Test
    fun onNewIntent_invalidIntent_showNetworkErrorAlertDialog() = runTest {
        val scheme = "test"
        val host = "test"
        val code = "ioException"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
        activity.startActivity(intent)

        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(CONNECTION_FAIL).isDisplayed()
        }
    }

    @Test
    fun onNewIntent_invalidIntent_showLoginFailureAlertDialog() = runTest {
        val scheme = "test"
        val host = "test"
        val code = "else"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
        activity.startActivity(intent)

        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(LOGIN_FAIL).isDisplayed()
        }
    }

    private fun setContent() {
        composeTestRule.setContent {
            LoginScreen(
                onNavigateToMain = { isMainScreen = true }
            )
        }
    }
}