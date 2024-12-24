package com.prac.feature.detail.view

import com.prac.data.model.RepoDetailModel

sealed interface UiState {
    data object Loading : UiState
    data class Content(val repository: RepoDetailModel = RepoDetailModel()) : UiState
    data class Error(val message: String = "") : UiState
}