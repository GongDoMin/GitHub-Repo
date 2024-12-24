package com.prac.feature.main

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.refresh.RefreshState
import com.prac.feature.main.view.UiState

internal class MainReducerProcessor : Reducer<Mutation, UiState> {
    override fun invoke(mutation: Mutation, currentState: UiState): UiState =
        when (mutation) {
            is Mutation.ShowContent -> showContent()
            is Mutation.ShowError -> showError(mutation.errorMessage)
            is Mutation.UpdateRefreshState -> updateRefreshState(mutation.refreshState)
        }

    private fun showError(errorMessage: String) =
        UiState.Error(errorMessage)

    private fun showContent() =
        UiState.Content()

    private fun updateRefreshState(refreshState: RefreshState) =
        UiState.Content(refreshState)
}