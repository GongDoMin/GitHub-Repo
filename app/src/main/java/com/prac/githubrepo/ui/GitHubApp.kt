package com.prac.githubrepo.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.prac.core.navigation.NavigationActions
import com.prac.core.navigation.Routes
import com.prac.core.navigation.Routes.LOGIN
import com.prac.feature.bottom.BottomNavItem
import com.prac.feature.bottom.GitHubBottomNavigation
import com.prac.feature.detail.navigation.detailScreen
import com.prac.feature.login.navigation.loginScreen
import com.prac.feature.main.navigation.mainScreen
import com.prac.feature.profile.navigation.profileScreen

@Composable
fun GitHubApp(
    navController: NavHostController = rememberNavController(),
    navigationActions: NavigationActions = remember(navController) { NavigationActions(navController) },
    startDestination: Routes = LOGIN
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val bottomNavItemList = listOf(BottomNavItem.Home, BottomNavItem.Profile)
    val currentBottomNavItem = remember(navBackStackEntry) {
        mutableStateOf(navBackStackEntry?.destination.getCurrentBottomNavItem(bottomNavItemList))
    }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        bottomBar = {
            currentBottomNavItem.value?.let {
                GitHubBottomNavigation(
                    onNavigationToBottom = navigationActions::navigateToBottom,
                    bottomNavItemList = bottomNavItemList,
                    currentBottomNavItem = it
                )
            }
        }
    ) { values ->
        NavHost(
            modifier = Modifier
                .padding(values),
            navController = navController,
            startDestination = startDestination,
        ) {
            loginScreen(
                onNavigateToMain = { navigationActions.navigateToBottom(Routes.HOME) },
            )

            navigation<Routes.HOME>(startDestination = Routes.HOME.MAIN) {
                mainScreen(
                    onNavigateToLogin = navigationActions::navigateToLogin,
                    onNavigateToDetail = navigationActions::navigateToDetail
                )

                detailScreen(
                    onNavigateToLogin = navigationActions::navigateToLogin,
                    onNavigateToBackStack = navigationActions::popBackStack
                )
            }

            profileScreen(
                onNavigateToLogin = navigationActions::navigateToLogin
            )
        }
    }
}

private fun NavDestination?.getCurrentBottomNavItem(bottomNavItemList: List<BottomNavItem>) : BottomNavItem? {
    bottomNavItemList.forEach { bottomNavItem ->
        if (this?.hierarchy?.any { it.hasRoute(bottomNavItem.route::class) } == true) {
            return bottomNavItem
        }
    }

    return null
}