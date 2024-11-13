package com.prac.githubrepo.ui.home.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val MAIN_SCREEN = "main"

fun NavController.navigationToMain() {
    navigate(MAIN_SCREEN) {
        popBackStack()
    }
}

fun NavGraphBuilder.mainScreen(
    onLogout: () -> Unit,
    onClickRepository: (String, String) -> Unit,
    onClickSetting: () -> Unit
) {
    composable(
        route = MAIN_SCREEN
    ) {
        MainScreen(
            onLogout = onLogout,
            onClickRepository = onClickRepository,
            onClickSetting = onClickSetting
        )
    }
}