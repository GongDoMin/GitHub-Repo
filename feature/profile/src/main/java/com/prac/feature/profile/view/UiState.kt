package com.prac.feature.profile.view

sealed interface UiState {
    data object Idle : UiState
    data object Loading : UiState
    data object Dialog : UiState
}