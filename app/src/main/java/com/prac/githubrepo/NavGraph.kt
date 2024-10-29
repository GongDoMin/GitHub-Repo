package com.prac.githubrepo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.prac.githubrepo.DetailArgs.REPO_NAME_ARG
import com.prac.githubrepo.DetailArgs.USER_NAME_ARG
import com.prac.githubrepo.login.LoginScreen
import com.prac.githubrepo.main.MainScreen
import com.prac.githubrepo.main.detail.DetailScreen
import com.prac.githubrepo.main.setting.SettingScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    navActions: NavigationActions = remember(navController) { NavigationActions(navController) },
    startDestination: String = Destinations.LOGIN_SCREEN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(
            route = Destinations.LOGIN_SCREEN
        ) {
            LoginScreen(
                onLogin = { navActions.navigationLoginToMain() }
            )
        }

        composable(
            route = Destinations.MAIN_SCREEN
        ) {
            MainScreen(
                onLogout = { navActions.navigationToLogin() },
                onClickRepository = { userName, repoName -> navActions.navigateMainToDetail(userName, repoName) },
                onClickSetting = { navActions.navigateMainToSetting() }
            )
        }

        composable(
            route = Destinations.DETAIL_SCREEN,
            arguments = listOf(
                navArgument(USER_NAME_ARG) { type = NavType.StringType },
                navArgument(REPO_NAME_ARG) { type = NavType.StringType },
            )
        ) { entry ->
            DetailScreen(
                onLogout = { navActions.navigationToLogin() },
                onBack = { navController.popBackStack() },
                userName = entry.arguments?.getString(USER_NAME_ARG),
                repoName = entry.arguments?.getString(REPO_NAME_ARG)
            )
        }

        composable(
            route = Destinations.SETTING_SCREEN
        ) {
            SettingScreen(
                onLogout = { navActions.navigationToLogin() }
            )
        }
    }
}