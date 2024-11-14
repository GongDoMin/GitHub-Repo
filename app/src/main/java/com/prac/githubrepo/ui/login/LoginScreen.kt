package com.prac.githubrepo.ui.login

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.prac.githubrepo.BuildConfig
import com.prac.githubrepo.R
import com.prac.githubrepo.util.BounceButton
import com.prac.githubrepo.util.ErrorAlertDialog
import com.prac.githubrepo.util.LoadingContent
import com.prac.githubrepo.util.drawableID

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as ComponentActivity

    LoginContent(
        isLoading = uiState is LoginViewModel.UiState.Loading,
        errorMessage = (uiState as? LoginViewModel.UiState.Error)?.errorMessage ?: "",
        onClickLogin = {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.GITHUB_OAUTH_URI))
            )
        },
        onDismissRequest = { viewModel.setUiState(LoginViewModel.UiState.Idle) }
    )

    LaunchedEffect(activity) {
        activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.event.collect {
                onNavigateToMain()
            }
        }
    }

    DisposableEffect(activity) {
        val listener = Consumer<Intent> {
            it.let { intent ->
                if (intent.action == Intent.ACTION_VIEW) {
                    intent.data?.let { uri ->
                        uri.getQueryParameter("code")?.let { code ->
                            viewModel.loginWithGitHub(code)
                        }
                    }
                }
            }
        }

        activity.addOnNewIntentListener(listener)

        onDispose { activity.removeOnNewIntentListener(listener) }
    }
}

@Composable
fun LoginContent(
    isLoading: Boolean,
    errorMessage: String,
    onClickLogin: () -> Unit,
    onDismissRequest: (String) -> Unit
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
                    )
                    .semantics { drawableID = R.drawable.img_github_icon },
                painter = painterResource(id = R.drawable.img_github_icon),
                contentDescription = null
            )

            BounceButton(
                text = stringResource(id = R.string.login),
                onClickButton = onClickLogin
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
