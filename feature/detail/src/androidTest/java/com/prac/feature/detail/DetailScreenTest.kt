package com.prac.feature.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.SavedStateHandle
import com.prac.core.common.backoff.BackOffWorkManager
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.designsystem.R
import com.prac.core.navigation.Routes.HOME.DETAIL.Companion.REPO_NAME
import com.prac.core.navigation.Routes.HOME.DETAIL.Companion.USER_NAME
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.feature.detail.view.DetailScreen
import com.prac.shared_test.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DetailScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity

    private lateinit var viewModel: DetailViewModel

    private val detailReducerProcessor = DetailReducerProcessor()

    private var isMainScreen = false

    @Inject lateinit var repoRepository: RepoRepository
    @Inject lateinit var tokenRepository: TokenRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun displayDetailScreen_uiStateIsError_showNotFoundRepositoryAlertDialog_and_navigateToMainScreen() = runTest {
        // 데이터 형식은 아래과 같음
        // RepoEntity(id = 0, name = "test 0", owner = OwnerEntity("login 0", "avatarUrl 0"), stargazersCount = 5, defaultBranch = "master", updatedAt = "update", isStarred = true),
        // RepoEntity(id = 1, name = "test 1", owner = OwnerEntity("login 1", "avatarUrl 1"), stargazersCount = 5, defaultBranch = "master", updatedAt = "update", isStarred = false),
        // ....
        viewModel = DetailViewModel(
            repoRepository = repoRepository,
            tokenRepository = tokenRepository,
            backOffWorkManager = FakeBackOffWorkManager(),
            detailReducerProcessor = detailReducerProcessor,
            ioDispatcher = Dispatchers.IO,
            savedStateHandle = SavedStateHandle().apply {
                set(USER_NAME, "login -1")
                set(REPO_NAME, "test -1")
            }
        )

        composeTestRule.setContent {
            DetailScreen(
                viewModel = viewModel,
                onNavigateToLogin = { },
                onNavigateToBackStack = { isMainScreen = true }
            )
        }

        composeTestRule.onNodeWithText(INVALID_REPOSITORY).assertIsDisplayed()

        composeTestRule.onNodeWithText(activity.getString(R.string.check)).performClick()

        composeTestRule.waitUntil {
            isMainScreen
        }
    }

    @Test
    fun clickStarImageView_starImageDrawableToUnStarImageDrawable_starCountMinusOne() {
        viewModel = DetailViewModel(
            repoRepository = repoRepository,
            tokenRepository = tokenRepository,
            backOffWorkManager = FakeBackOffWorkManager(),
            detailReducerProcessor = detailReducerProcessor,
            ioDispatcher = Dispatchers.IO,
            savedStateHandle = SavedStateHandle().apply {
                set(USER_NAME, "login 0")
                set(REPO_NAME, "test 0")
            }
        )
        val expectedStarCount = 4
        composeTestRule.setContent {
            DetailScreen(
                viewModel = viewModel,
                onNavigateToLogin = { },
                onNavigateToBackStack = { }
            )
        }

        composeTestRule
            .onNode(hasContentDescription("image is star"))
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(expectedStarCount.toString()).isDisplayed()
                    && composeTestRule.onNode(hasContentDescription("image is unstar")).isDisplayed()
        }
    }

    @Test
    fun clickUnStarImageView_unStarImageDrawableToStarImageDrawable_starCountMinusOne() {
        viewModel = DetailViewModel(
            repoRepository = repoRepository,
            tokenRepository = tokenRepository,
            backOffWorkManager = FakeBackOffWorkManager(),
            detailReducerProcessor = detailReducerProcessor,
            ioDispatcher = Dispatchers.IO,
            savedStateHandle = SavedStateHandle().apply {
                set(USER_NAME, "login 1")
                set(REPO_NAME, "test 1")
            }
        )
        val expectedStarCount = 6
        composeTestRule.setContent {
            DetailScreen(
                viewModel = viewModel,
                onNavigateToLogin = { },
                onNavigateToBackStack = { }
            )
        }

        composeTestRule
            .onNode(hasContentDescription("image is unstar"))
            .performClick()

        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(expectedStarCount.toString()).isDisplayed()
                    && composeTestRule.onNode(hasContentDescription("image is star")).isDisplayed()
        }
    }

    private class FakeBackOffWorkManager : BackOffWorkManager {
        override fun addWork(uniqueID: String, times: Int, initialDelay: Long, maxDelay: Long, factor: Double, work: suspend () -> Result<*>) {
            TODO("Not yet implemented")
        }

        override fun clearWork() {
            TODO("Not yet implemented")
        }
    }
}