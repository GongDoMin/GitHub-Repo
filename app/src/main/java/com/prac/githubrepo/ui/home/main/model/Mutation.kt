package com.prac.githubrepo.ui.home.main.model

import androidx.paging.LoadState
import com.prac.data.entity.RepoEntity

sealed interface Mutation {
    data class UpdateRepositories(
        val repositories: List<RepoEntity>,
        val loadState: LoadState,
    ) : Mutation
    data object ShowRepositories : Mutation
    data class ShowError(val errorMessage: String) : Mutation
}