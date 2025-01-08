package com.prac.feature.main

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.prac.core.designsystem.R
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
    private val activity get() = composeTestRule.activity

    private var isDetailScreen = false
    private var userName = ""
    private var repoName = ""

    @Before
    fun setup() {
        hiltRule.inject()
        setContent()
    }

    @Test
    fun 스타이미지클릭_언스타이미지_및_스타개수변경() {
        // given
        val clickPosition = 0 // position 이 짝수일 경우 repository is starred
        val expectedStarCount = 4
        composeTestRule
            .onNode(hasContentDescription(activity.getString(R.string.lazy_column_description)))
            .performScrollToIndex(clickPosition)
            .assertIsDisplayed()

        // when
        composeTestRule
            .onNodeWithText(getRepositoryName(clickPosition))
            .onChild()
            .assert(hasContentDescription(activity.getString(R.string.star_image_description)))
            .performClick()

        // then
        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithText(getRepositoryName(clickPosition))
                .onChild()
                .isChangedStarStateAndCount(hasContentDescription(activity.getString(R.string.unstar_image_description)), expectedStarCount)
        }
    }

    @Test
    fun 언스타이미지클릭_스타이미지_및_스타개수변경() {
        // given
        val clickPosition = 1 // position 이 홀수일 경우 repository is unStarred
        val expectedStarCount = 6
        composeTestRule
            .onNode(hasContentDescription(activity.getString(R.string.lazy_column_description)))
            .performScrollToIndex(clickPosition)
            .assertIsDisplayed()

        // when
        composeTestRule
            .onNodeWithText(getRepositoryName(clickPosition))
            .onChild()
            .assert(hasContentDescription(activity.getString(R.string.unstar_image_description)))
            .performClick()

        // then
        composeTestRule.waitUntil {
            composeTestRule
                .onNodeWithText(getRepositoryName(clickPosition))
                .onChild()
                .isChangedStarStateAndCount(hasContentDescription(activity.getString(R.string.star_image_description)), expectedStarCount)
        }
    }

    @Test
    fun 레파지토리클릭_레파지토리상세화면으로이동() = runTest {
        // given
        val clickPosition = 0
        val expectedUserName = "login 0"
        val expectedRepoName = "test 0"
        composeTestRule
            .onNode(hasContentDescription(activity.getString(R.string.lazy_column_description)))
            .performScrollToIndex(clickPosition)
            .assertIsDisplayed()

        // when
        composeTestRule
            .onNodeWithText(getRepositoryName(clickPosition))
            .performClick()

        // then
        composeTestRule.awaitIdle()
        assertTrue(isDetailScreen)
        assertEquals(userName, expectedUserName)
        assertEquals(repoName, expectedRepoName)
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

    private fun getRepositoryName(position: Int) = "test $position"

    private fun SemanticsNodeInteraction.isChangedStarStateAndCount(starMatcher: SemanticsMatcher, expectedStarCount: Int) : Boolean {
        val node = fetchSemanticsNode()

        return starMatcher.matches(node) and hasText(expectedStarCount.toString()).matches(node.parent ?: throw NullPointerException())
    }
}