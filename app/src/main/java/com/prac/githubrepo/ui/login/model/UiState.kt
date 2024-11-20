package com.prac.githubrepo.ui.login.model

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data class Error(val errorMessage : String) : UiState()
}