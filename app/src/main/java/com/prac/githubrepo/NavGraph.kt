package com.prac.githubrepo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.prac.githubrepo.login.LOGIN_SCREEN
import com.prac.githubrepo.login.loginScreen
import com.prac.githubrepo.login.navigationToLogin
import com.prac.githubrepo.main.MAIN_SCREEN
import com.prac.githubrepo.main.detail.detailScreen
import com.prac.githubrepo.main.detail.navigationToDetail
import com.prac.githubrepo.main.mainScreen
import com.prac.githubrepo.main.navigationMain
import com.prac.githubrepo.main.setting.navigationToSetting
import com.prac.githubrepo.main.setting.settingScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = LOGIN_SCREEN
) {
    val loginNavOptions = remember {
        navOptions {
            popUpTo(MAIN_SCREEN) {
                inclusive = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        loginScreen(
            onLogin = { navController.navigationMain() }
        )

        mainScreen(
            onLogout = {
                navController.navigationToLogin(navOptions = loginNavOptions)
            },
            onClickRepository = { userName, repoName ->
                navController.navigationToDetail(userName, repoName)
            },
            onClickSetting = { navController.navigationToSetting() }
        )

        detailScreen(
            onLogout = {
                navController.navigationToLogin(navOptions = loginNavOptions)
            },
            onBack = { navController.popBackStack() }
        )

        settingScreen(
            onLogout = {
                navController.navigationToLogin(navOptions = loginNavOptions)
            }
        )
    }
}