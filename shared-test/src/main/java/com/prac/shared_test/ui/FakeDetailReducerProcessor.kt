package com.prac.shared_test.ui

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.entity.RepoDetailEntity
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.view.UiState

class FakeDetailReducerProcessor : Reducer<Mutation, UiState> {
    override fun invoke(mutation: Mutation, currentState: UiState): com.prac.feature.detail.view.UiState =
        when (mutation) {
            is Mutation.ShowRepository -> currentState.showRepository(mutation.repository)
            is Mutation.ShowLoading -> currentState.showLoading()
            is Mutation.ShowError -> currentState.showError(mutation.errorMessage)
            is Mutation.DismissError -> currentState.dismissError()
        }

    private fun UiState.showRepository(repository: RepoDetailEntity) =
        copy(
            repository = repository,
            isLoading = false,
            isError = false,
            errorMessage = ""
        )

    private fun UiState.showLoading() =
        copy(
            isLoading = true,
            isError = false,
            errorMessage = ""
        )

    private fun UiState.showError(errorMessage: String) =
        copy(
            isLoading = false,
            isError = true,
            errorMessage = errorMessage
        )

    private fun UiState.dismissError() =
        copy(
            isError = false,
            errorMessage = ""
        )
}