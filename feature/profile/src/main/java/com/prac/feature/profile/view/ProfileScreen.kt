package com.prac.feature.profile.view

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.prac.core.common.ui.ConfirmationDialog
import com.prac.core.common.ui.LoadingIndicator
import com.prac.core.designsystem.R
import com.prac.feature.profile.ProfileViewModel
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event

@Composable
internal fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    when (uiState) {
        is UiState.Idle -> {
            ProfileContent(
                onClickLogoutButton = { viewModel.process(Action.UserAction.OnClickLogoutButton) },
                modifier = modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.padding_normal))
            )
        }
        is UiState.Loading -> {
            LoadingIndicator(modifier = loadingModifier)
        }
        is UiState.Dialog -> {
            ConfirmationDialog(
                onDismissRequest = { viewModel.process(Action.UserAction.DialogDismiss) },
                message = stringResource(id = R.string.logout_confirm),
                negativeButtonText = stringResource(id = R.string.cancel),
                onClickNegativeButton = { viewModel.process(Action.UserAction.OnClickNegativeButton) },
                positiveButtonText = stringResource(id = R.string.check),
                onClickPositiveButton = { viewModel.process(Action.UserAction.OnClickPositiveButton) }
            )
        }
    }

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