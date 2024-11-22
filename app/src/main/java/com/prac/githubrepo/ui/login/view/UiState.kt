package com.prac.githubrepo.ui.login.view

data class UiState(
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var errorMessage: String = ""
)