package com.prac.githubrepo

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.prac.core.designsystem.R
import com.prac.core.navigation.Routes.HOME
import com.prac.core.navigation.Routes.PROFILE
import com.prac.githubrepo.ui.GitHubApp
import com.prac.shared_test.HiltTestActivity
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
            GitHubApp(navController = navController)
        }

        val scheme = "test"
        val host = "test"
        val code = "success"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("$scheme://$host?code=$code"))
        activity.startActivity(intent)

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.parent?.hasRoute(HOME::class) == true
                    && navController.currentBackStackEntry?.destination?.hasRoute(HOME.MAIN::class) == true
        }
    }

    @Test
    fun navigationMainToDetailTest() = runTest {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            GitHubApp(
                startDestination = HOME,
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
            navController.currentBackStackEntry?.destination?.hasRoute(HOME.DETAIL::class) == true
        }
    }

    @Test
    fun navigationBottomHomeToBottomProfile() = runTest {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            GitHubApp(
                startDestination = HOME,
                navController = navController
            )
        }

        composeTestRule
            .onNodeWithText(activity.getString(R.string.bottom_profile))
            .performClick()

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.hasRoute(PROFILE::class) == true
        }
    }

    @Test
    fun navigationBottomProfileToBottomHome() = runTest {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            GitHubApp(
                startDestination = PROFILE,
                navController = navController
            )
        }

        composeTestRule
            .onNodeWithText(activity.getString(R.string.bottom_home))
            .performClick()

        composeTestRule.waitUntil {
            navController.currentBackStackEntry?.destination?.parent?.hasRoute(HOME::class) == true
        }
    }
}