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
import com.prac.githubrepo.ui.NavigationDestinations.LOGIN
import com.prac.githubrepo.ui.home.homeNavigation
import com.prac.githubrepo.ui.login.loginScreen
import com.prac.githubrepo.ui.profile.view.profileScreen
import com.prac.githubrepo.components.GitHubBottomNavigation

@Composable
fun GitHubApp(
    navController: NavHostController = rememberNavController(),
    navigationActions: NavigationActions = remember(navController) { NavigationActions(navController) },
    startDestination: NavigationDestinations = LOGIN
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
                navigationActions = navigationActions
            )

            homeNavigation(
                navigationActions = navigationActions
            )

            profileScreen(
                navigationActions = navigationActions
            )
        }
    }
}