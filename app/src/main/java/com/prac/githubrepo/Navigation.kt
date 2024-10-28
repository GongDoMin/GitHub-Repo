package com.prac.githubrepo

import androidx.navigation.NavHostController
import com.prac.githubrepo.DetailArgs.REPO_NAME_ARG
import com.prac.githubrepo.DetailArgs.USER_NAME_ARG

private object Screens {
    const val LOGIN_SCREEN = "login"
    const val MAIN_SCREEN = "main"
    const val DETAIL_SCREEN = "detail"
    const val SETTING_SCREEN = "setting"
}

object DetailArgs {
    const val USER_NAME_ARG = "userName"
    const val REPO_NAME_ARG = "repoName"
}

object Destinations {
    const val LOGIN_SCREEN = Screens.LOGIN_SCREEN
    const val MAIN_SCREEN = Screens.MAIN_SCREEN
    const val DETAIL_SCREEN = "${Screens.DETAIL_SCREEN}?${USER_NAME_ARG}={${USER_NAME_ARG}}&${REPO_NAME_ARG}={${REPO_NAME_ARG}}"
    const val SETTING_SCREEN = Screens.SETTING_SCREEN
}

class NavigationActions(private val navController: NavHostController) {

    fun navigationLoginToMain() {
        navController.navigate(Screens.MAIN_SCREEN) {
            popUpTo(Screens.LOGIN_SCREEN) { inclusive = true }
        }
    }

    fun navigationToLogin() {
        navController.navigate(Screens.LOGIN_SCREEN) {
            popUpTo(Screens.MAIN_SCREEN) { inclusive = true }
        }
    }

    fun navigateMainToDetail(userName: String, repoName: String) {
        val route = "${Screens.DETAIL_SCREEN}?${USER_NAME_ARG}=$userName&${REPO_NAME_ARG}=$repoName"
        navController.navigate(
            route
        )
    }

    fun navigateMainToSetting() {
        navController.navigate(Screens.SETTING_SCREEN)
    }
}
