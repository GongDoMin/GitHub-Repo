package com.prac.githubrepo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prac.githubrepo.ui.home.homeNavigation
import com.prac.githubrepo.ui.home.main.navigationToMain
import com.prac.githubrepo.ui.login.LOGIN_SCREEN
import com.prac.githubrepo.ui.login.loginScreen
import com.prac.githubrepo.ui.profile.profileScreen
import com.prac.githubrepo.util.MyBottomNavigation

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = LOGIN_SCREEN
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        bottomBar = {
            val currentDestination = navBackStackEntry?.destination?.route

            if (currentDestination == LOGIN_SCREEN) return@Scaffold

            MyBottomNavigation(navController = navController)
        }
    ) { values ->
        NavHost(
            modifier = Modifier
                .padding(values),
            navController = navController,
            startDestination = startDestination,
        ) {
            loginScreen(
                onLogin = { navController.navigationToMain() }
            )

            homeNavigation(
                navController = navController
            )

            profileScreen()
        }
    }
}