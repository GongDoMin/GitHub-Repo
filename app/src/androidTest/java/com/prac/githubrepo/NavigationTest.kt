package com.prac.githubrepo

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.espresso.Espresso.pressBack
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.main.MAIN_SCREEN
import com.prac.githubrepo.main.detail.DETAIL_SCREEN_WITH_ARGS
import com.prac.githubrepo.main.setting.SETTING_SCREEN
import com.prac.githubrepo.util.hasButton
import com.prac.githubrepo.util.hasDrawable
import com.prac.githubrepo.util.hasIcon
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()
    private val activity get() = composeTestRule.activity
    private lateinit var navController: TestNavHostController

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun navigationLoginToMainTest() = runTest {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavGraph(navController = navController)
        }

        val scheme = "test"
        val host = "test"
        val code = "success"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
        activity.startActivity(intent)

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.route == MAIN_SCREEN
                    && composeTestRule.onNodeWithText(activity.getString(R.string.repository)).isDisplayed()
        }
    }

    @Test
    fun navigationMainToDetailTest() = runTest {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavGraph(
                startDestination = MAIN_SCREEN,
                navController = navController
            )
        }

        val clickPosition = 0
        composeTestRule
            .onNode(hasTestTag("lazyColumn"))
            .performScrollToIndex(clickPosition)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("test $clickPosition")
            .performClick()

        val expectedRepoDetail = RepoEntity(id = 0, name = "test 0", owner = OwnerEntity("login 0", "avatarUrl 0"), stargazersCount = 5, defaultBranch = "master", updatedAt = "update", isStarred = true)

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.route == DETAIL_SCREEN_WITH_ARGS
                    && composeTestRule.onNodeWithText(expectedRepoDetail.name).isDisplayed()
                    && composeTestRule.onNodeWithText(expectedRepoDetail.owner.login).isDisplayed()
                    && composeTestRule.onNode(hasDrawable(R.drawable.img_glide_profile)).isDisplayed()
                    && composeTestRule.onNode(hasDrawable(R.drawable.img_star)).isDisplayed()
                    && composeTestRule.onNode(hasDrawable(R.drawable.img_fork)).isDisplayed()
        }
    }

    @Test
    fun navigationDetailToMainTest() = runTest {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavGraph(
                startDestination = MAIN_SCREEN,
                navController = navController
            )
        }

        val clickPosition = 0
        composeTestRule
            .onNode(hasTestTag("lazyColumn"))
            .performScrollToIndex(clickPosition)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("test $clickPosition")
            .performClick()

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.route == DETAIL_SCREEN_WITH_ARGS
        }

        pressBack()

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.route == MAIN_SCREEN
                    && composeTestRule.onNodeWithText(activity.getString(R.string.repository)).isDisplayed()
        }
    }

    @Test
    fun navigationMainToSettingTest() = runTest {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavGraph(
                startDestination = MAIN_SCREEN,
                navController = navController
            )
        }

        composeTestRule
            .onNode(hasIcon(Icons.Default.AccountCircle))
            .performClick()

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.route == SETTING_SCREEN
                    && composeTestRule.onNode(hasButton(activity.getString(R.string.logout))).isDisplayed()
        }
    }

    @Test
    fun navigationSettingToMainTest() = runTest {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            NavGraph(
                startDestination = MAIN_SCREEN,
                navController = navController
            )
        }

        composeTestRule
            .onNode(hasIcon(Icons.Default.AccountCircle))
            .performClick()

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.route == SETTING_SCREEN
        }

        pressBack()

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.route == MAIN_SCREEN
                    && composeTestRule.onNodeWithText(activity.getString(R.string.repository)).isDisplayed()
        }
    }
}