package com.prac.feature.detail.view

import com.prac.data.model.RepositoryDetail

sealed interface UiState {
    data object Loading : UiState
    data class Content(val repository: RepositoryDetail = RepositoryDetail()) : UiState
    data class Error(val message: String = "") : UiState
}