package com.prac.feature.detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.core.navigation.Routes.HOME
import com.prac.feature.detail.view.DetailScreen

fun NavGraphBuilder.detailScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
) {
    composable<HOME.DETAIL> {
        DetailScreen(
            onNavigateToLogin = onLogout,
            onBack = onBack
        )
    }
}