package com.prac.githubrepo.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.R
import com.prac.githubrepo.constants.INVALID_TOKEN
import com.prac.githubrepo.databinding.ActivityMainBinding
import com.prac.githubrepo.login.LoginActivity
import com.prac.githubrepo.main.MainViewModel.SideEffect
import com.prac.githubrepo.main.detail.DetailActivity
import com.prac.githubrepo.main.request.StarStateRequestBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    @Inject lateinit var starStateRequestBuilderFactory: StarStateRequestBuilder.Factory
    private val mainAdapter: MainAdapter by lazy {
        MainAdapter(
            starStateRequestBuilderFactory.create(this.lifecycleScope),
            object : MainAdapter.OnRepositoryClickListener {
                override fun clickRepository(repoEntity: RepoEntity) {
                    viewModel.setSideEffect(SideEffect.RepositoryClick(repoEntity))
                }

                override fun star(repoEntity: RepoEntity) {
                    viewModel.setSideEffect(SideEffect.StarClick(repoEntity))
                }

                override fun unStar(repoEntity: RepoEntity) {
                    viewModel.setSideEffect(SideEffect.UnStarClick(repoEntity))
                }
            }
        )
    }
    private val retryFooterAdapter: RetryFooterAdapter by lazy { RetryFooterAdapter { mainAdapter.retry() } }
    private val conCatAdapter: ConcatAdapter by lazy { ConcatAdapter(mainAdapter, retryFooterAdapter) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMain.apply {
            adapter = conCatAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainAdapter.loadStateFlow.collect {
                    viewModel.handleLoadStates(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
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

    private suspend fun MainViewModel.Content.handleUiState() {
        if (dialogMessage.isNotEmpty()) {
            AlertDialog.Builder(this@MainActivity)
                .setMessage(dialogMessage)
                .setPositiveButton(R.string.check) { dialog, _ ->
                    dialog.dismiss()
                }
                .setOnDismissListener {
                    handleDialogMessage(dialogMessage)
                }
                .show()
        }

        this.loadState?.let { retryFooterAdapter.loadState = it }

        mainAdapter.submitData(this.repositories.first())
    }

    private fun SideEffect.handleSideEffect() {
        when (this) {
            is SideEffect.LogoutDialogDismiss -> {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)

                finish()
            }
            is SideEffect.StarDialogDismiss -> { }
            is SideEffect.StarClick -> {
                viewModel.starRepository(repoEntity)
            }

            is SideEffect.UnStarClick -> {
                viewModel.unStarRepository(repoEntity)
            }

            is SideEffect.RepositoryClick -> {
                val intent = DetailActivity.createIntent(this@MainActivity, repoEntity.owner.login, repoEntity.name)

                startActivity(intent)
            }
        }
    }

    private fun handleDialogMessage(dialogMessage: String) {
        if (dialogMessage == INVALID_TOKEN) {
            viewModel.setSideEffect(SideEffect.LogoutDialogDismiss)
            return
        }

        viewModel.setSideEffect(SideEffect.StarDialogDismiss)
    }
}