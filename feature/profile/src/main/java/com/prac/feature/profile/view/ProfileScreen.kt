package com.prac.feature.profile.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.prac.feature.profile.ProfileViewModel
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    ProfileContent(
        isLoading = uiState.isLoading,
        isDialog = uiState.isDialog,
        onClickLogoutButton = { viewModel.process(Action.UserAction.OnClickLogoutButton) },
        onClickCheckButton = { viewModel.process(Action.UserAction.OnClickCheckButton) },
        onDismissRequest = { viewModel.process(Action.UserAction.DialogDismiss) }
    )

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect {
                when (it) {
                    is Event.Logout -> onNavigateToLogin()
                }
            }
        }
    }
}