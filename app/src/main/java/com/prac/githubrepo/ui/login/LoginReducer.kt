package com.prac.githubrepo.ui.login

import com.prac.githubrepo.common.Reducer
import com.prac.githubrepo.ui.login.model.Mutation
import com.prac.githubrepo.ui.login.view.UiState

class LoginReducer : Reducer<Mutation, UiState> {
    override fun invoke(mutation: Mutation, currentState: UiState): UiState =
        when (mutation) {
            is Mutation.ShowIdle -> currentState.showIdle()
            is Mutation.ShowLoading -> currentState.showLoading()
            is Mutation.ShowError -> currentState.showError(mutation.errorMessage)
        }

    private fun UiState.showIdle() =
        copy(
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
}