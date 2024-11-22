package com.prac.githubrepo.ui.login.view

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.prac.githubrepo.BuildConfig
import com.prac.githubrepo.ui.login.LoginViewModel
import com.prac.githubrepo.ui.login.model.Action
import com.prac.githubrepo.ui.login.model.Event
import com.prac.githubrepo.ui.login.model.UiState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val activity = LocalContext.current as ComponentActivity

    LoginContent(
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onClickLogin = { viewModel.process(Action.OnClickLoginButton) },
        onDismissRequest = { viewModel.process(Action.DialogDismiss) }
    )

    LaunchedEffect(activity) {
        activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect {
                when (it) {
                    is Event.LoginSuccess -> onNavigateToMain()
                    is Event.LaunchLoginIntent -> {
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
                            viewModel.process(Action.OAuthAuthenticated(code))
                        }
                    }
                }
            }
        }

        activity.addOnNewIntentListener(listener)

        onDispose { activity.removeOnNewIntentListener(listener) }
    }
}
