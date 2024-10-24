package com.prac.githubrepo.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prac.githubrepo.R
import com.prac.githubrepo.util.ErrorAlertDialog
import com.prac.githubrepo.util.LoadingContent
import com.prac.githubrepo.util.bounceClick

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        isLoading = uiState is LoginViewModel.UiState.Loading,
        errorMessage = (uiState as? LoginViewModel.UiState.Error)?.errorMessage ?: "",
        onDismissRequest = { viewModel.setSideEffect(LoginViewModel.SideEffect.ErrorAlertDialogDismiss) },
        onLoginButtonClick = { viewModel.setSideEffect(LoginViewModel.SideEffect.LoginButtonClick) }
    )
}

@Composable
fun LoginContent(
    isLoading: Boolean,
    errorMessage: String,
    onDismissRequest: (String) -> Unit,
    onLoginButtonClick: () -> Unit
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.login_icon))
                    .padding(
                        bottom = dimensionResource(id = R.dimen.padding_normal)
                    ),
                painter = painterResource(id = R.drawable.img_github_icon),
                contentDescription = null
            )

            LoginButton(
                onLoginButtonClick = onLoginButtonClick
            )

            Text(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_small)
                    ),
                text = stringResource(id = R.string.login_description)
            )
        }

        if (errorMessage.isNotEmpty()) {
            ErrorAlertDialog(
                onDismissRequest = onDismissRequest,
                errorMessage = errorMessage
            )
        }
    }
}

@Composable
fun LoginButton(
    onLoginButtonClick: () -> Unit
) {
    Button(
        onClick = onLoginButtonClick,
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(),
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
            text = stringResource(id = R.string.login)
        )
    }
}