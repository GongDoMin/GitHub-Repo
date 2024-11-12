package com.prac.githubrepo.ui.home.main.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.prac.githubrepo.R
import com.prac.githubrepo.util.BasicAlertDialog
import com.prac.githubrepo.util.BounceButton
import com.prac.githubrepo.util.LoadingContent

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    SettingContent(
        isLoading = uiState is SettingViewModel.UiState.Loading,
        dialogMessage = (uiState as? SettingViewModel.UiState.Dialog)?.message ?: "",
        onClickLogoutButton = {
            viewModel.setUiState(SettingViewModel.UiState.Dialog(message = context.getString(R.string.logout_confirm)))
        },
        onClickCheckButton = { viewModel.logout() },
        onDismissRequest = { viewModel.setUiState(SettingViewModel.UiState.Idle) }
    )

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.event.collect {
                onLogout()
            }
        }
    }
}

@Composable
fun SettingContent(
    isLoading: Boolean,
    dialogMessage: String,
    onClickLogoutButton: () -> Unit,
    onClickCheckButton: () -> Unit,
    onDismissRequest: () -> Unit
) {
    LoadingContent(
        isLoading = isLoading
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = dimensionResource(id = R.dimen.padding_normal),
                    end = dimensionResource(id = R.dimen.padding_normal)
                ),
            verticalArrangement = Arrangement.Center
        ) {
            BounceButton(
                text = stringResource(id = R.string.logout),
                onClickButton = onClickLogoutButton
            )
        }

        if (dialogMessage.isNotEmpty()) {
            BasicAlertDialog(
                onDismissRequest = onDismissRequest,
                onClickCheckButton = onClickCheckButton,
                dialogMessage = dialogMessage
            )
        }
    }
}