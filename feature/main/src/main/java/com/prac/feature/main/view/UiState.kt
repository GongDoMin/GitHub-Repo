package com.prac.feature.main.view

import com.prac.feature.main.refresh.RefreshState

data class UiState(
    val isError: Boolean = false,
    val errorMessage: String = "",
    val refreshState: RefreshState = RefreshState.Default
)