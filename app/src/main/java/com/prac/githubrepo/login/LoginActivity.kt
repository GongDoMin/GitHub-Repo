package com.prac.githubrepo.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.prac.githubrepo.BuildConfig
import com.prac.githubrepo.R
import com.prac.githubrepo.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
    }

    private fun startGitHubOAuth() {
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