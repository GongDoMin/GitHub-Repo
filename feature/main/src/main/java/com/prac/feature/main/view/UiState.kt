package com.prac.feature.main.view

import androidx.paging.LoadState
import com.prac.data.entity.RepoEntity

data class UiState(
    val repositories: List<RepoEntity> = emptyList(),
    val loadState: LoadState = LoadState.NotLoading(false),
    val isError: Boolean = false,
    val errorMessage: String = ""
)