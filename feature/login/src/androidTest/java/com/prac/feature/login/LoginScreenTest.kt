package com.prac.feature.login

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.LOGIN_FAIL
import com.prac.core.designsystem.R
import com.prac.feature.login.view.LoginScreen
import com.prac.shared_test.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginScreenTest {

    private var isMainScreen: Boolean = false

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    @Before
    fun 초기화() {
        hiltRule.inject()
        Intents.init()
        setContent()
    }

    @After
    fun 정리() {
        Intents.release()
    }

    @Test
    fun 로그인버튼클릭_브라우저오픈() = runTest {
        // given
        intending(not(isInternal()))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

        // when
        composeTestRule.onNodeWithText(activity.getString(R.string.login)).performClick()

        // then
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(Uri.parse(BuildConfig.GITHUB_OAUTH_URI))
            )
        )
    }

    @Test
    fun 유효한인텐트_메인액티비티로_이동() = runTest {
        // given
        val scheme = "test"
        val host = "test"
        val code = "success"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))

        // when
        activity.startActivity(intent)

        // then
        composeTestRule.waitUntil {
            isMainScreen
        }
    }

    @Test
    fun IOException으로인해_다이어로그를_보여줌() = runTest {
        // given
        val scheme = "test"
        val host = "test"
        val code = "ioException"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))

        // when
        activity.startActivity(intent)

        // then
        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(CONNECTION_FAIL).isDisplayed()
        }
    }

    @Test
    fun 에러로인해_다이어로그를_보여줌() = runTest {
        // given
        val scheme = "test"
        val host = "test"
        val code = "else"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))

        // when
        activity.startActivity(intent)

        // then
        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(LOGIN_FAIL).isDisplayed()
        }
    }

    @Test
    fun 다이어로그해제_아이들상태를_보여줌() = runTest {
        // given
        val scheme = "test"
        val host = "test"
        val code = "else"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
        activity.startActivity(intent)
        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(LOGIN_FAIL).isDisplayed()
        }

        // when
        composeTestRule.onNodeWithText(activity.getString(R.string.check)).performClick()

        // then
        composeTestRule.onNodeWithText(LOGIN_FAIL).assertIsNotDisplayed()
    }

    private fun setContent() {
        composeTestRule.setContent {
            LoginScreen(
                onNavigateToMain = { isMainScreen = true }
            )
        }
    }
}