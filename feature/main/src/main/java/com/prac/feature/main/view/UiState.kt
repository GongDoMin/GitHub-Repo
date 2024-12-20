package com.prac.feature.main.view

import com.prac.feature.main.refresh.RefreshState

sealed interface UiState {
    data class Content(val refreshState: RefreshState = RefreshState.Default) : UiState
    data class Error(val message: String = "") : UiState
}