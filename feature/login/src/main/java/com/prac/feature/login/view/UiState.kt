package com.prac.feature.login.view

data class UiState(
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var errorMessage: String = ""
)