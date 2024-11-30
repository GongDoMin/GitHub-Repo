package com.prac.feature.login.view

data class UiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)