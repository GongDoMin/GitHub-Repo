package com.prac.githubrepo.ui.profile

import com.prac.githubrepo.common.Reducer
import com.prac.githubrepo.ui.profile.model.Mutation
import com.prac.githubrepo.ui.profile.view.UiState

class ProfileReducerProcessor : Reducer<Mutation, UiState> {
    override fun invoke(mutation: Mutation, currentState: UiState): UiState =
        when (mutation) {
            is Mutation.ShowIdle -> currentState.showIdle()
            is Mutation.ShowLoading -> currentState.showLoading()
            is Mutation.ShowDialog -> currentState.showDialog()
        }

    private fun UiState.showIdle() =
        copy(
            isLoading = false,
            isDialog = false
        )

    private fun UiState.showLoading() =
        copy(
            isLoading = true,
            isDialog = false
        )

    private fun UiState.showDialog() =
        copy(
            isLoading = false,
            isDialog = true
        )
}