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
        setContentView(binding.root)

        handleOnBackPressed()

        viewModel.getRepository(
            intent.getStringExtra(USER_NAME),
            intent.getStringExtra(REPO_NAME)
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    it.handleUiState()
                }
            }
        }

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

    private fun UiState.handleUiState() {
        when (this) {
            is UiState.Idle -> {}
            is UiState.Loading -> {
                binding.includeProgressBar.root.isVisible = true
            }
            is UiState.Content -> {
                binding.includeProgressBar.root.isVisible = false

                bindRepositoryDetail(this.repository)
                setOnStarClickListener(this.repository)
            }
            is UiState.Error -> {
                binding.includeProgressBar.root.isVisible = false

                AlertDialog.Builder(this@DetailActivity)
                    .setMessage(this.errorMessage)
                    .setPositiveButton(R.string.check) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setOnDismissListener {
                        handleDialogMessage(errorMessage)
                    }
                    .show()
            }
        }
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

    private fun bindRepositoryDetail(repoDetailEntity: RepoDetailEntity) {
        Glide.with(binding.root)
            .load(repoDetailEntity.owner.avatarUrl)
            .error(R.drawable.img_glide_error)
            .placeholder(R.drawable.img_glide_profile)
            .into(binding.ivProfile)

        binding.tvName.text = repoDetailEntity.owner.login
        binding.tvTitle.text = repoDetailEntity.name
        binding.ivStar.setImageResource(
            if (repoDetailEntity.isStarred == true) R.drawable.img_star
            else R.drawable.img_unstar
        )
        binding.tvStarCount.text = getString(R.string.star_count, repoDetailEntity.stargazersCount)
        binding.tvForkCount.text = getString(R.string.fork_count, repoDetailEntity.forksCount)
    }

    private fun setOnStarClickListener(repoDetailEntity: RepoDetailEntity) {
        binding.ivStar.setOnClickListener {
            if (repoDetailEntity.isStarred == true) viewModel.setSideEffect(SideEffect.UnStarClick(repoDetailEntity))
            else viewModel.setSideEffect(SideEffect.StarClick(repoDetailEntity))
        }
    }

    private fun handleDialogMessage(dialogMessage: String) {
        if (dialogMessage == CONNECTION_FAIL || dialogMessage == INVALID_REPOSITORY) {
            viewModel.setSideEffect(SideEffect.BasicDialogDismiss)
            return
        }

        viewModel.setSideEffect(SideEffect.LogoutDialogDismiss)
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