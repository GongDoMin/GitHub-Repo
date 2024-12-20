package com.prac.feature.login.view

sealed interface UiState {
    data object Idle: UiState
    data object Loading: UiState
    data class Error(val message: String = ""): UiState
}