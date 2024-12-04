package com.prac.feature.profile.view

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.prac.core.designsystem.R
import com.prac.feature.profile.ProfileViewModel
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event

@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    ProfileContent(
        isLoading = uiState.isLoading,
        isDialog = uiState.isDialog,
        onClickLogoutButton = { viewModel.process(Action.UserAction.OnClickLogoutButton) },
        onClickNegativeButton = { viewModel.process(Action.UserAction.OnClickNegativeButton) },
        onClickPositiveButton = { viewModel.process(Action.UserAction.OnClickPositiveButton) },
        onDismissRequest = { viewModel.process(Action.UserAction.DialogDismiss) },
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.padding_normal))
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