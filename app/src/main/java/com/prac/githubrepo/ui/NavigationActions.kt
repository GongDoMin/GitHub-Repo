package com.prac.githubrepo.ui

import androidx.navigation.NavController
import com.prac.githubrepo.ui.Routes.HOME
import com.prac.githubrepo.ui.Routes.LOGIN
import com.prac.githubrepo.ui.Routes.PROFILE

class NavigationActions(private val navController: NavController) {
    fun popBackStack() {
        navController.popBackStack()
    }

    fun navigateToLogin() {
        navController.navigate(LOGIN) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    fun navigateToBottom(route: Routes) {
        when (route) {
            HOME -> {
                navController.navigate(route) {
                    popUpTo(navController.graph.id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            PROFILE -> {
                navController.navigate(route) {
                    popUpTo(navController.graph.id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            else -> throw IllegalArgumentException("Invalid route")
        }
    }

    fun navigateToMain() {
        navController.navigate(HOME.MAIN) {
            popBackStack()
        }
    }

    fun navigateToDetail(userName: String, repoName: String) {
        navController.navigate(HOME.DETAIL(userName, repoName))
    }
}