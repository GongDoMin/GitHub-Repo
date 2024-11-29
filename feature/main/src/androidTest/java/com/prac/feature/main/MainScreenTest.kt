package com.prac.feature.main

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.prac.feature.main.view.MainScreen
import com.prac.shared_test.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private var isDetailScreen = false
    private var userName = ""
    private var repoName = ""

    @Before
    fun setup() {
        hiltRule.inject()
        setContent()
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
            .assert(hasContentDescription("image is star"))
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithText("test $clickPosition")
                .onChild()
                .isChangedStarStateAndCount(hasContentDescription("image is unstar"), expectedStarCount)
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
            .assert(hasContentDescription("image is unstar"))
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithText("test $clickPosition")
                .onChild()
                .isChangedStarStateAndCount(hasContentDescription("image is star"), expectedStarCount)
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
                onNavigateToLogin = { },
                onClickRepository = { userName, repoName ->
                    isDetailScreen = true
                    this.userName = userName
                    this.repoName = repoName
                }
            )
        }
    }

    private fun SemanticsNodeInteraction.isChangedStarStateAndCount(starMatcher: SemanticsMatcher, expectedStarCount: Int) : Boolean {
        val node = fetchSemanticsNode()

        return starMatcher.matches(node) and hasText(expectedStarCount.toString()).matches(node.parent ?: throw NullPointerException())
    }
}