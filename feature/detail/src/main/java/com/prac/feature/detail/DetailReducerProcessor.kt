package com.prac.feature.detail

import com.prac.data.model.RepoDetailModel
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.view.UiState

class DetailReducerProcessor : Reducer<Mutation, UiState> {
    override fun invoke(mutation: Mutation, currentState: UiState): UiState =
        when (mutation) {
            is Mutation.ShowRepository -> showRepository(mutation.repository)
            is Mutation.ShowLoading -> showLoading()
            is Mutation.ShowError -> showError(mutation.errorMessage)
        }

    private fun showRepository(repository: RepoDetailModel) =
        UiState.Content(repository)

    private fun showLoading() =
        UiState.Loading

    private fun showError(errorMessage: String) =
        UiState.Error(errorMessage)
}