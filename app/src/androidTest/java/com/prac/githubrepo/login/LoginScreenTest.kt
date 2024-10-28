package com.prac.githubrepo.login

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.BuildConfig
import com.prac.githubrepo.MainActivity
import com.prac.githubrepo.R
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.LOGIN_FAIL
import com.prac.githubrepo.util.hasButton
import com.prac.githubrepo.util.hasDrawable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LoginScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val activity get() = composeTestRule.activity

    private var isMainScreen: Boolean = false

    @Inject
    lateinit var tokenRepository: TokenRepository

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
    fun displayLoginScreen_whenUiStateIsIdle() {
        composeTestRule.onNode(hasDrawable(R.drawable.img_github_icon)).assertIsDisplayed()
        composeTestRule.onNode(hasButton(R.string.login)).assertIsDisplayed()
        composeTestRule.onNodeWithText((activity.getString(R.string.login))).assertIsDisplayed()
        composeTestRule.onNodeWithText(activity.getString(R.string.login_description)).assertIsDisplayed()
    }

    @Test
    fun loginButtonClick_openBrowser() = runTest {
        composeTestRule.onNode(hasButton(R.string.login)).performClick()

        composeTestRule.awaitIdle()

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse(BuildConfig.GITHUB_OAUTH_URI)))
    }

    @Test
    fun onNewIntent_validIntent_navigateToMainActivity() = runTest {
        val scheme = "githubrepo"
        val host = "localhost:8080"
        val code = "success"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
        activity.startActivity(intent)

        composeTestRule.awaitIdle()

        assertTrue(isMainScreen)
    }

    @Test
    fun onNewIntent_invalidIntent_showNetworkErrorAlertDialog() = runTest {
        val scheme = "githubrepo"
        val host = "localhost:8080"
        val code = "ioException"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
        activity.startActivity(intent)

        composeTestRule.awaitIdle()

        composeTestRule.onNodeWithText(CONNECTION_FAIL).assertExists()
    }

    @Test
    fun onNewIntent_invalidIntent_showLoginFailureAlertDialog() = runTest {
        val scheme = "githubrepo"
        val host = "localhost:8080"
        val code = "else"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
        activity.startActivity(intent)

        composeTestRule.awaitIdle()

        composeTestRule.onNodeWithText(LOGIN_FAIL).assertExists()
    }

    private fun setContent() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = LoginViewModel(tokenRepository, Dispatchers.IO),
                onLogin = { isMainScreen = true }
            )
        }
    }
}