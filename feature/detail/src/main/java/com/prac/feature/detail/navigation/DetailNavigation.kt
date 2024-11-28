package com.prac.feature.detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prac.core.navigation.Routes.HOME
import com.prac.feature.detail.view.DetailScreen

fun NavGraphBuilder.detailScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToBackStack: () -> Unit,
) {
    composable<HOME.DETAIL> {
        DetailScreen(
            onNavigateToLogin = onNavigateToLogin,
            onBack = onNavigateToBackStack
        )
    }
}