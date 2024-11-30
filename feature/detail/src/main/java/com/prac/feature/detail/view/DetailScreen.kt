package com.prac.feature.detail.view

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.prac.core.designsystem.R
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.feature.detail.DetailViewModel
import com.prac.feature.detail.model.Action
import com.prac.feature.detail.model.Event

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToBackStack: () -> Unit
) {
    val uiState = viewModel.uiStateFlow.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DetailContent(
        isLoading = uiState.value.isLoading,
        isError = uiState.value.isError,
        errorMessage = uiState.value.errorMessage,
        repoDetail = uiState.value.repository,
        onClickStar = { viewModel.process(Action.UserAction.OnClickStar(it)) },
        onClickUnStar = { viewModel.process(Action.UserAction.OnClickUnStar(it)) },
        onDismissRequest = { dialogMessage ->
            if (dialogMessage == CONNECTION_FAIL || dialogMessage == INVALID_REPOSITORY) viewModel.process(
                Action.UserAction.DialogDismiss)
            else viewModel.process(Action.UserAction.LogoutDialogDismiss)
        },
        modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_small))
    )

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect {
                when (it) {
                    is Event.Error -> onNavigateToBackStack()
                    is Event.Logout -> {
                        viewModel.logout()
                        onNavigateToLogin()
                    }
                }
            }
        }
    }
}