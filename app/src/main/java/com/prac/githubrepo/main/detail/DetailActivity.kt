package com.prac.githubrepo.main.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.prac.data.entity.RepoDetailEntity
import com.prac.githubrepo.R
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.INVALID_REPOSITORY
import com.prac.githubrepo.databinding.ActivityDetailBinding
import com.prac.githubrepo.login.LoginActivity
import com.prac.githubrepo.main.detail.DetailViewModel.UiState
import com.prac.githubrepo.main.detail.DetailViewModel.SideEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        binding.composeView.setContent {
            DetailScreen()
        }
        setContentView(binding.root)

        handleOnBackPressed()

        viewModel.getRepository(
            intent.getStringExtra(USER_NAME),
            intent.getStringExtra(REPO_NAME)
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect {
                    it.handleSideEffect()
                }
            }
        }
    }

    private fun handleOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun SideEffect.handleSideEffect() {
        when (this) {
            is SideEffect.BasicDialogDismiss -> {
                finish()
            }
            is SideEffect.LogoutDialogDismiss -> {
                val intent = Intent(this@DetailActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            }
            is SideEffect.StarClick -> {
                viewModel.starRepository(repoDetailEntity)
            }
            is SideEffect.UnStarClick -> {
                viewModel.unStarRepository(repoDetailEntity)
            }
        }
    }

    companion object {
        const val USER_NAME = "userName"
        const val REPO_NAME = "repoName"

        fun createIntent(context: Context, userName: String, repoName: String) : Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(USER_NAME, userName)
                putExtra(REPO_NAME, repoName)
            }
        }
    }

}