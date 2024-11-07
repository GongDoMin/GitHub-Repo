package com.prac.githubrepo.main.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SETTING_SCREEN = "setting"

fun NavController.navigationToSetting() {
    navigate(SETTING_SCREEN)
}

fun NavGraphBuilder.settingScreen(
    onLogout: () -> Unit,
) {
    composable(
        route = SETTING_SCREEN
    ) {
        SettingScreen(
            onLogout = onLogout
        )
    }
}
