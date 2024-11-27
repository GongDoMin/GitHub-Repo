package com.prac.feature.detail.view

import com.prac.data.entity.RepoDetailEntity

data class UiState(
    var repository: RepoDetailEntity = RepoDetailEntity(),
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var errorMessage: String = ""
)