package com.prac.githubrepo.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prac.core.navigation.NavigationActions
import com.prac.core.navigation.Routes
import com.prac.core.navigation.Routes.LOGIN
import com.prac.feature.home.navigation.homeNavigation
import com.prac.feature.login.navigation.loginScreen
import com.prac.feature.profile.navigation.profileScreen

@Composable
fun GitHubApp(
    navController: NavHostController = rememberNavController(),
    navigationActions: NavigationActions = remember(navController) { NavigationActions(navController) },
    startDestination: Routes = LOGIN
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        bottomBar = {
            if (navBackStackEntry?.destination?.hasRoute(LOGIN::class) == true) return@Scaffold

            GitHubBottomNavigation(
                navigationActions = navigationActions,
                currentDestination = navBackStackEntry?.destination
            )
        }
    ) { values ->
        NavHost(
            modifier = Modifier
                .padding(values),
            navController = navController,
            startDestination = startDestination,
        ) {
            loginScreen(
                onNavigateToMain = navigationActions::navigateToMain,
            )

            homeNavigation(
                onNavigateToLogin = navigationActions::navigateToLogin,
                onNavigateToDetail = navigationActions::navigateToDetail,
                onNavigateToBackStack = navigationActions::popBackStack
            )

            profileScreen(
                navigationActions = navigationActions
            )
        }
    }
}