package com.prac.feature.login.view

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.prac.core.common.ui.LoadingIndicator
import com.prac.core.common.ui.MessageDialog
import com.prac.core.designsystem.R
import com.prac.feature.login.BuildConfig
import com.prac.feature.login.LoginViewModel
import com.prac.feature.login.model.Action
import com.prac.feature.login.model.Event

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    modifier: Modifier = Modifier,
    loginModifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val activity = LocalContext.current as ComponentActivity
    val lifecycleOwner = LocalLifecycleOwner.current

    when (uiState) {
        is UiState.Idle -> {
            LoginContent(
                onClickLoginButton = { viewModel.process(Action.UserAction.OnClickLoginButton) },
                modifier = modifier
                    .padding(dimensionResource(id = R.dimen.padding_normal))
            )
        }
        is UiState.Loading -> {
            LoadingIndicator(modifier = loginModifier)
        }
        is UiState.Error -> {
            MessageDialog(
                onDismissRequest = { viewModel.process(Action.UserAction.DialogDismiss) },
                message = (uiState as UiState.Error).message,
                confirmButtonText = stringResource(id = R.string.check)
            )
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect {
                when (it) {
                    is Event.SuccessLogin -> onNavigateToMain()
                    is Event.OpenBrowser -> {
                        activity.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.GITHUB_OAUTH_URI))
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(activity) {
        val listener = Consumer<Intent> {
            it.let { intent ->
                if (intent.action == Intent.ACTION_VIEW) {
                    intent.data?.let { uri ->
                        uri.getQueryParameter("code")?.let { code ->
                            viewModel.process(Action.InternalAction.AuthenticateOAuth(code))
                        }
                    }
                }
            }
        }

        activity.addOnNewIntentListener(listener)

        onDispose { activity.removeOnNewIntentListener(listener) }
    }
}
