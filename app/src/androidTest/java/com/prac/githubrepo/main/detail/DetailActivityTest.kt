package com.prac.githubrepo.main.detail

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prac.githubrepo.R
import com.prac.githubrepo.main.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CustomTypeSafeMatcher
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DetailActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var starDrawable: Drawable
    private lateinit var unStarDrawable: Drawable

    @Before
    fun setup() {
        hiltRule.inject()
        activityRule.scenario.onActivity {
            starDrawable = it.getDrawable(R.drawable.img_star)!!
            unStarDrawable = it.getDrawable(R.drawable.img_unstar)!!
        }
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun detailActivity_displayInUI() {
        val clickPosition = 0
        val expectedRepoName = "test 0"
        val expectedUserName = "login 0"
        val expectedStarCount = "별 5개"
        val expectedForkCount = "포크 5개"
        val expectedStarDrawable = starDrawable
        onView(withId(R.id.rvMain))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(clickPosition, ViewActions.click()))
        intended(hasComponent(DetailActivity::class.java.name))

        onView(withText(expectedRepoName)).check(matches(isDisplayed()))
        onView(withText(expectedUserName)).check(matches(isDisplayed()))
        onView(withText(expectedStarCount)).check(matches(isDisplayed()))
        onView(withText(expectedForkCount)).check(matches(isDisplayed()))
        onView(withId(R.id.ivStar)).check(matches(matchesImageViewDrawable(expectedStarDrawable)))
    }

    @Test
    fun detailActivity_clickStarImageView() {
        val clickPosition = 0
        val expectedStarDrawable = unStarDrawable
        onView(withId(R.id.rvMain))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(clickPosition, ViewActions.click()))
        intended(hasComponent(DetailActivity::class.java.name))

        onView(withId(R.id.ivStar))
            .perform(waitForImageChange())
            .check(matches(matchesImageViewDrawable(expectedStarDrawable)))
    }

    @Test
    fun detailActivity_clickStarUnImageView() {
        val clickPosition = 1
        val expectedStarDrawable = starDrawable
        onView(withId(R.id.rvMain))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(clickPosition, ViewActions.click()))
        intended(hasComponent(DetailActivity::class.java.name))

        onView(withId(R.id.ivStar))
            .perform(waitForImageChange())
            .check(matches(matchesImageViewDrawable(expectedStarDrawable)))
    }

    private fun matchesImageViewDrawable(drawable: Drawable): Matcher<View> {
        return object : CustomTypeSafeMatcher<View>("get matched view is ImageView and view drawable") {
            override fun matchesSafely(item: View): Boolean =
                (item as ImageView).drawable?.constantState == drawable.constantState
        }
    }

    private fun waitForImageChange(): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String
                    = "wait for image change"

            override fun getConstraints(): Matcher<View> =
                allOf(ViewMatchers.isAssignableFrom(ImageView::class.java), isDisplayed())

            override fun perform(uiController: UiController?, view: View?) {
                view?.performClick()
                // io 작업을 대기
                uiController?.loopMainThreadForAtLeast(500)
            }
        }
    }
}