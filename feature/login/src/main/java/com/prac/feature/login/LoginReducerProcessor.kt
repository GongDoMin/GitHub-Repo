package com.prac.feature.login

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.feature.login.model.Mutation
import com.prac.feature.login.view.UiState

internal class LoginReducerProcessor : Reducer<Mutation, UiState> {
    override fun invoke(mutation: Mutation, currentState: UiState): UiState =
        when (mutation) {
            is Mutation.ShowIdle -> showIdle()
            is Mutation.ShowLoading -> showLoading()
            is Mutation.ShowError -> showError(mutation.errorMessage)
        }

    private fun showIdle() =
        UiState.Idle

    private fun showLoading() =
        UiState.Loading

    private fun showError(errorMessage: String) =
        UiState.Error(errorMessage)
}