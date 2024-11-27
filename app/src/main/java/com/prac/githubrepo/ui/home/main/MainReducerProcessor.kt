package com.prac.githubrepo.ui.home.main

import androidx.paging.LoadState
import com.prac.data.entity.RepoEntity
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.githubrepo.ui.home.main.model.Mutation
import com.prac.githubrepo.ui.home.main.view.UiState


class MainReducerProcessor : Reducer<Mutation, UiState> {
    override fun invoke(mutation: Mutation, currentState: UiState): UiState =
        when (mutation) {
            is Mutation.UpdateRepositories -> currentState.updateRepositories(mutation.repositories, mutation.loadState)
            is Mutation.ShowError -> currentState.showError(mutation.errorMessage)
            is Mutation.ShowRepositories -> currentState.showRepositories()
        }

    private fun UiState.updateRepositories(repositories: List<RepoEntity>, loadState: LoadState) =
        copy(
            repositories = repositories,
            loadState = loadState
        )

    private fun UiState.showError(errorMessage: String) =
        copy(
            isError = true,
            errorMessage = errorMessage
        )

    private fun UiState.showRepositories() =
        copy(
            isError = false,
            errorMessage = ""
        )
}