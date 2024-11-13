package com.prac.githubrepo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.prac.githubrepo.ui.home.main.detail.detailScreen
import com.prac.githubrepo.ui.home.main.detail.navigationToDetail
import com.prac.githubrepo.ui.home.main.mainScreen
import com.prac.githubrepo.ui.home.main.navigationToMain
import com.prac.githubrepo.ui.home.main.setting.navigationToSetting
import com.prac.githubrepo.ui.home.main.setting.settingScreen
import com.prac.githubrepo.ui.login.LOGIN_SCREEN
import com.prac.githubrepo.ui.login.loginScreen
import com.prac.githubrepo.ui.login.navigationToLogin

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = LOGIN_SCREEN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        loginScreen(
            onLogin = { navController.navigationToMain() }
        )

        mainScreen(
            onLogout = {
                navController.navigationToLogin()
            },
            onClickRepository = { userName, repoName ->
                navController.navigationToDetail(userName, repoName)
            },
            onClickSetting = { navController.navigationToSetting() }
        )

        detailScreen(
            onLogout = {
                navController.navigationToLogin()
            },
            onBack = { navController.popBackStack() }
        )

        settingScreen(
            onLogout = {
                navController.navigationToLogin()
            }
        )
    }
}