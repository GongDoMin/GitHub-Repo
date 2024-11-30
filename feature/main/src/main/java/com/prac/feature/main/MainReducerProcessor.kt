package com.prac.feature.main

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.view.UiState

class MainReducerProcessor : Reducer<Mutation, UiState> {
    override fun invoke(mutation: Mutation, currentState: UiState): UiState =
        when (mutation) {
            is Mutation.ShowError -> currentState.showError(mutation.errorMessage)
            is Mutation.ShowRepositories -> currentState.showRepositories()
        }

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