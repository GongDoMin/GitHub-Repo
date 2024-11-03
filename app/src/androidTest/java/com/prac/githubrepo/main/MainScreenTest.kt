package com.prac.githubrepo.main

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.MainActivity
import com.prac.githubrepo.R
import com.prac.githubrepo.util.BackOffWorkManager
import com.prac.githubrepo.util.hasDrawable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
class MainScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private var isDetailScreen = false
    private var userName = ""
    private var repoName = ""

    @Before
    fun setup() {
        hiltRule.inject()
        Intents.init()
        setContent()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun clickStarImageView_starImageDrawableToUnStarImageDrawable_starCountMinusOne() {
        val clickPosition = 0 // position 이 짝수일 경우 repository is starred
        val expectedStarCount = 4

        composeTestRule
            .onNode(hasTestTag("lazyColumn"))
            .performScrollToIndex(clickPosition)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("test $clickPosition")
            .onChild()
            .assert(hasDrawable(R.drawable.img_star))
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithText("test $clickPosition")
                .onChild()
                .isChangedStarStateAndCount(hasDrawable(R.drawable.img_unstar), expectedStarCount)
        }
    }

    @Test
    fun clickUnStarImageView_unStarImageDrawableToStarImageDrawable_starCountPlusOne() {
        val clickPosition = 1 // position 이 짝수일 경우 repository is starred
        val expectedStarCount = 6

        composeTestRule
            .onNode(hasTestTag("lazyColumn"))
            .performScrollToIndex(clickPosition)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("test $clickPosition")
            .onChild()
            .assert(hasDrawable(R.drawable.img_unstar))
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithText("test $clickPosition")
                .onChild()
                .isChangedStarStateAndCount(hasDrawable(R.drawable.img_star), expectedStarCount)
        }
    }

    @Test
    fun clickRepository_navigateToDetailActivity() = runTest {
        val clickPosition = 0

        composeTestRule
            .onNode(hasTestTag("lazyColumn"))
            .performScrollToIndex(clickPosition)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("test $clickPosition")
            .performClick()

        composeTestRule.awaitIdle()

        assertTrue(isDetailScreen)
        assertEquals(userName, "login 0")
        assertEquals(repoName, "test 0")
    }

    private fun setContent() {
        composeTestRule.setContent {
            MainScreen(
                onLogout = { },
                onClickRepository = { userName, repoName ->
                    isDetailScreen = true
                    this.userName = userName
                    this.repoName = repoName
                },
                onClickSetting = { }
            )
        }
    }

    private fun SemanticsNodeInteraction.isChangedStarStateAndCount(starMatcher: SemanticsMatcher, expectedStarCount: Int) : Boolean {
        val node = fetchSemanticsNode()

        return starMatcher.matches(node) and hasText(expectedStarCount.toString()).matches(node.parent ?: throw NullPointerException())
    }
}