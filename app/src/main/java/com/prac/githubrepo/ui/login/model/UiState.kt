package com.prac.githubrepo.ui.login.model

data class UiState(
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var errorMessage: String = ""
)