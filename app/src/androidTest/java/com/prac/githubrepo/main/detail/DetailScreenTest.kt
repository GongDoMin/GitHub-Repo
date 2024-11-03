package com.prac.githubrepo.main.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import com.prac.githubrepo.MainActivity
import com.prac.githubrepo.R
import com.prac.githubrepo.constants.INVALID_REPOSITORY
import com.prac.githubrepo.util.hasDrawable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DetailScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val activity get() = composeTestRule.activity

    private var isMainScreen = false

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
    fun displayDetailScreen_uiStateIsError_showNotFoundRepositoryAlertDialog_and_navigateToMainScreen() = runTest {
        // 데이터 형식은 아래과 같음
        // RepoEntity(id = 0, name = "test 0", owner = OwnerEntity("login 0", "avatarUrl 0"), stargazersCount = 5, defaultBranch = "master", updatedAt = "update", isStarred = true),
        // RepoEntity(id = 1, name = "test 1", owner = OwnerEntity("login 1", "avatarUrl 1"), stargazersCount = 5, defaultBranch = "master", updatedAt = "update", isStarred = false),
        // ....
        val userName = "test -1"
        val repoName = "login -1"
        composeTestRule.setContent {
            DetailScreen(
                onLogout = { },
                onBack = { isMainScreen = true },
                userName = userName,
                repoName = repoName
            )
        }

        composeTestRule.onNodeWithText(INVALID_REPOSITORY).assertIsDisplayed()

        composeTestRule.onNodeWithText(activity.getString(R.string.check)).performClick()

        composeTestRule.awaitIdle()

        assertTrue(isMainScreen)
    }

    @Test
    fun clickStarImageView_starImageDrawableToUnStarImageDrawable_starCountMinusOne() {
        val userName = "test 0"
        val repoName = "login 0"
        val expectedStarCount = 4
        composeTestRule.setContent {
            DetailScreen(
                onLogout = { },
                onBack = { },
                userName = userName,
                repoName = repoName
            )
        }

        composeTestRule
            .onNode(hasDrawable(R.drawable.img_star))
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(expectedStarCount.toString()).isDisplayed()
                    && composeTestRule.onNode(hasDrawable(R.drawable.img_unstar)).isDisplayed()
        }
    }

    @Test
    fun clickUnStarImageView_unStarImageDrawableToStarImageDrawable_starCountMinusOne() {
        val userName = "test 1"
        val repoName = "login 1"
        val expectedStarCount = 6
        composeTestRule.setContent {
            DetailScreen(
                onLogout = { },
                onBack = { },
                userName = userName,
                repoName = repoName
            )
        }

        composeTestRule
            .onNode(hasDrawable(R.drawable.img_unstar))
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(expectedStarCount.toString()).isDisplayed()
                    && composeTestRule.onNode(hasDrawable(R.drawable.img_star)).isDisplayed()
        }
    }
}