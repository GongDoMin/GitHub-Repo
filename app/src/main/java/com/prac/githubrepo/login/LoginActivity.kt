package com.prac.githubrepo.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.prac.githubrepo.BuildConfig
import com.prac.githubrepo.databinding.ActivityLoginBinding
import com.prac.githubrepo.login.LoginViewModel.Event
import com.prac.githubrepo.login.LoginViewModel.SideEffect
import com.prac.githubrepo.login.LoginViewModel.UiState
import com.prac.githubrepo.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.composeView.setContent {
            LoginScreen(viewModel)
        }
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    handleEvent(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect {
                    handleSideEffect(it)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.Success -> {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)

                finish()
            }
        }
    }

    private fun handleSideEffect(sideEffect: SideEffect) {
        when (sideEffect) {
            is SideEffect.LoginButtonClick -> {
                login()
            }
            is SideEffect.ErrorAlertDialogDismiss -> {
                viewModel.setUiState(UiState.Idle)
            }
        }
    }

    private fun login() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.GITHUB_OAUTH_URI))
        startActivity(intent)
    }

    private fun handleIntent(receiveIntent: Intent?) {
        receiveIntent?.let { intent ->
            if (intent.action == Intent.ACTION_VIEW) {
                intent.data?.let { uri ->
                    uri.getQueryParameter("code")?.let { code ->
                        viewModel.loginWithGitHub(code)
                    }
                }
            }
        }
    }
}

