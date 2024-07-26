package com.prac.githubrepo.main

import android.provider.Contacts.Intents.UI
import androidx.lifecycle.ViewModel
import com.prac.data.entity.RepoEntity
import com.prac.data.repository.RepoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repoRepository: RepoRepository
): ViewModel() {
    sealed class UiState {
        data object Idle : UiState()

        data object Loading : UiState()

        data class Success(
            val repositories : List<RepoEntity>
        ) : UiState()

        data class Error(
            val errorMessage: String
        ) : UiState()
    }
}