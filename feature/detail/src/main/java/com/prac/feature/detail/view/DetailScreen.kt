package com.prac.feature.detail.view

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.common.ui.LoadingIndicator
import com.prac.core.common.ui.MessageDialog
import com.prac.core.designsystem.R
import com.prac.feature.detail.DetailViewModel
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event

@Composable
fun DetailScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    when (uiState.value) {
        is UiState.Loading -> {
            LoadingIndicator(
                modifier = loadingModifier
            )
        }
        is UiState.Content -> {
            DetailContent(
                repoDetail = (uiState.value as UiState.Content).repository,
                onClickStar = { viewModel.process(Action.UserAction.OnClickStar(it)) },
                onClickUnStar = { viewModel.process(Action.UserAction.OnClickUnStar(it)) },
                modifier = modifier
                    .padding(dimensionResource(id = R.dimen.padding_normal))
            )
        }
        is UiState.Error -> {
            MessageDialog(
                onDismissRequest = { dialogMessage ->
                    if (dialogMessage == CONNECTION_FAIL || dialogMessage == INVALID_REPOSITORY) viewModel.process(
                        Action.UserAction.DialogDismiss)
                    else viewModel.process(Action.UserAction.LogoutDialogDismiss)
                },
                message = (uiState.value as UiState.Error).message,
                confirmButtonText = stringResource(id = R.string.check)
            )
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect {
                when (it) {
                    is Event.Error -> onNavigateToBackStack()
                    is Event.Logout -> onNavigateToLogin()
                }
            }
        }
    }
}