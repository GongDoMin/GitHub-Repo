package com.prac.githubrepo.ui.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val LOGIN_SCREEN = "login"

fun NavController.navigationToLogin(
    navOptions: NavOptions
) {
    navigate(LOGIN_SCREEN, navOptions)
}

fun NavGraphBuilder.loginScreen(
    onLogin: () -> Unit
) {
    composable(
        route = LOGIN_SCREEN
    ) {
        LoginScreen(
            onLogin = onLogin
        )
    }
}