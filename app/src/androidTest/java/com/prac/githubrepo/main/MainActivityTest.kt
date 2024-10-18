package com.prac.githubrepo.main

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prac.githubrepo.R
import com.prac.githubrepo.main.detail.DetailActivity
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
class MainActivityTest {

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
        Intents.init() // Espresso Intents 초기화
    }

    @After
    fun tearDown() {
        Intents.release() // Espresso Intents 해제
    }

    @Test
    fun mainActivity_displaysInUiWithInitialItem() {
        val initialItemCount = 10
        repeat(initialItemCount) {
            val expectedText = "login $it"
            val expectedDrawable = if (it % 2 == 0) starDrawable else unStarDrawable
            onView(withId(R.id.rvMain))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(it))
                .check(matches(hasDescendant(allOf(withText(expectedText), isDisplayed()))))
                .check(matches(matchesImageViewDrawableInRecyclerView(R.id.rvMain, it, expectedDrawable)))
        }
    }

    @Test
    fun repositoryIsNotEmpty_clickStarImageView() {
        val clickPosition = 0 // position 이 짝수일 경우 repository is starred

        onView(withRecyclerViewAtPosition(R.id.rvMain, clickPosition, R.id.ivStar))
            .perform(waitForImageChange())
            .check(matches(matchesImageViewDrawable(unStarDrawable)))
    }

    @Test
    fun repositoryIsNotEmpty_clickUnStarImageView() {
        val clickPosition = 1 // position 이 홀수일 경우 repository is starred

        onView(withRecyclerViewAtPosition(R.id.rvMain, clickPosition, R.id.ivStar))
            .perform(waitForImageChange())
            .check(matches(matchesImageViewDrawable(starDrawable)))
    }

    @Test
    fun repositoryIsNotEmpty_clickRepository() {
        val clickPosition = 0

        onView(withId(R.id.rvMain))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(clickPosition, ViewActions.click()))

        intended(hasComponent(DetailActivity::class.java.name))
    }

    private fun withRecyclerViewAtPosition(@IdRes recyclerViewId: Int, position: Int, @IdRes resId: Int? = null): Matcher<View> {
        onView(withId(recyclerViewId)).perform(
            scrollToPosition<RecyclerView.ViewHolder>(position)
        )
        return matchesViewInRecyclerView(recyclerViewId, position, resId)
    }

    private fun matchesViewInRecyclerView(@IdRes recyclerViewId: Int, position: Int, @IdRes resId: Int? = null): Matcher<View> {
        return object : CustomTypeSafeMatcher<View>("get matched view in recyclerView") {
            override fun matchesSafely(item: View): Boolean {
                val recyclerView = item.rootView.findViewById<RecyclerView>(recyclerViewId)
                val targetViewHolder = recyclerView.findViewHolderForAdapterPosition(position)

                return resId?.let {
                    val targetView = targetViewHolder?.itemView?.findViewById<View>(resId)
                    item == targetView
                } ?: run {
                    item == targetViewHolder?.itemView
                }
            }
        }
    }

    private fun matchesImageViewDrawableInRecyclerView(@IdRes recyclerViewId: Int, position: Int, drawable: Drawable): Matcher<View> {
        return object : CustomTypeSafeMatcher<View>("matches view drawable in recyclerView") {
            override fun matchesSafely(item: View): Boolean {
                val recyclerView = item.rootView.findViewById<RecyclerView>(recyclerViewId)
                val targetViewHolder = recyclerView.findViewHolderForAdapterPosition(position)

                return targetViewHolder?.itemView?.findViewById<ImageView>(R.id.ivStar)?.drawable?.constantState == drawable.constantState
            }
        }
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
                allOf(isAssignableFrom(ImageView::class.java), isDisplayed())

            override fun perform(uiController: UiController?, view: View?) {
                view?.performClick()
                // io 작업을 대기
                uiController?.loopMainThreadForAtLeast(500)
            }
        }
    }
}