package com.prac.githubrepo.ui

import androidx.navigation.NavController
import com.prac.core.navigation.NavigationDestinations
import com.prac.core.navigation.NavigationDestinations.HOME
import com.prac.core.navigation.NavigationDestinations.LOGIN
import com.prac.core.navigation.NavigationDestinations.PROFILE

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

    fun navigateToBottom(route: NavigationDestinations) {
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