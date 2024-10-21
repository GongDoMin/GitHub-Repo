package com.prac.githubrepo.login

import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.BuildConfig
import com.prac.githubrepo.R
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.LOGIN_FAIL
import com.prac.githubrepo.main.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.hamcrest.Matchers.endsWith
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LoginActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Before
    fun setup() {
        hiltRule.inject()
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun loginActivity_displayInUI() {
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun loginButtonClick_callLoginMethod() {
        onView(withId(R.id.btnLogin)).perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
        intended(hasData(Uri.parse(BuildConfig.GITHUB_OAUTH_URI)))
    }

    @Test
    fun onNewIntent_validIntent_navigateToMainActivity() {
        val scheme = "githubrepo"
        val host = "localhost:8080"
        val code = "success"

        activityRule.scenario.onActivity {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
            it.startActivity(intent)
        }

        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun onNewIntent_invalidIntent_showConnectionFailDialog() {
        val scheme = "githubrepo"
        val host = "localhost:8080"
        val code = "ioException"

        activityRule.scenario.onActivity {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
            it.startActivity(intent)
        }

        onView(ViewMatchers.withText(endsWith(CONNECTION_FAIL)))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withText(R.string.check))
            .perform(click())
        onView(ViewMatchers.withText(CoreMatchers.endsWith(CONNECTION_FAIL)))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun onNewIntent_invalidIntent_showLoginFailDialog() {
        val scheme = "githubrepo"
        val host = "localhost:8080"
        val code = "else"

        activityRule.scenario.onActivity {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
            it.startActivity(intent)
        }

        onView(ViewMatchers.withText(endsWith(LOGIN_FAIL)))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
        onView(ViewMatchers.withText(R.string.check))
            .perform(click())
        onView(ViewMatchers.withText(CoreMatchers.endsWith(LOGIN_FAIL)))
            .check(ViewAssertions.doesNotExist())
    }
}