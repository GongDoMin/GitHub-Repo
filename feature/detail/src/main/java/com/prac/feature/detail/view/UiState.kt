package com.prac.feature.detail.view

import com.prac.data.model.RepoDetailModel

data class UiState(
    var repository: RepoDetailModel = RepoDetailModel(),
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var errorMessage: String = ""
)