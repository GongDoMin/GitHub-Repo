package com.prac.githubrepo.main.setting

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.prac.githubrepo.R
import com.prac.githubrepo.util.BasicAlertDialog
import com.prac.githubrepo.util.LoadingContent
import com.prac.githubrepo.util.bounceClick
import com.prac.githubrepo.util.buttonID

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as ComponentActivity

    SettingContent(
        isLoading = uiState is SettingViewModel.UiState.Loading,
        dialogMessage = (uiState as? SettingViewModel.UiState.Dialog)?.message ?: "",
        onClickLogoutButton = {
            viewModel.setUiState(SettingViewModel.UiState.Dialog(message = "정말 로그아웃하시겠습니까?"))
        },
        onClickCheckButton = { viewModel.logout() },
        onDismissRequest = { viewModel.setUiState(SettingViewModel.UiState.Idle) }
    )

    LaunchedEffect(Unit) {
        activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            LogoutButton(
                onClickLogout = onClickLogoutButton
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

@Composable
fun LogoutButton(
    onClickLogout: () -> Unit
) {
    Button(
        onClick = onClickLogout,
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
            .semantics { buttonID = R.string.logout },
        colors = ButtonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        )
    ) {
        Text(
            modifier = Modifier
                .padding(
                    top = dimensionResource(id = R.dimen.padding_small),
                    bottom = dimensionResource(id = R.dimen.padding_small)
                ),
            text = stringResource(id = R.string.logout)
        )
    }
}