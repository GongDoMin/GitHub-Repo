package com.prac.feature.detail.view

import com.prac.data.model.RepoDetailModel

data class UiState(
    val repository: RepoDetailModel = RepoDetailModel(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)