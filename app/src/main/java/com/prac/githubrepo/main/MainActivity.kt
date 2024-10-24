package com.prac.githubrepo.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.prac.githubrepo.constants.INVALID_TOKEN
import com.prac.githubrepo.databinding.ActivityMainBinding
import com.prac.githubrepo.login.LoginActivity
import com.prac.githubrepo.main.MainViewModel.SideEffect
import com.prac.githubrepo.main.detail.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.composeView.setContent {
            MainScreen()
        }
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect {
                    it.handleSideEffect()
                }
            }
        }
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
                viewModel.unStarRepository(repoEntity)
            }

            is SideEffect.UnStarClick -> {
                viewModel.starRepository(repoEntity)
            }

            is SideEffect.RepositoryClick -> {
                val intent = DetailActivity.createIntent(this@MainActivity, repoEntity.owner.login, repoEntity.name)

                startActivity(intent)
            }
        }
    }
}