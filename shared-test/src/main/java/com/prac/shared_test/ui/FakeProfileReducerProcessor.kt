package com.prac.shared_test.ui

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.githubrepo.ui.profile.model.Mutation
import com.prac.githubrepo.ui.profile.view.UiState

class FakeProfileReducerProcessor : Reducer<Mutation, UiState> {
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
            isDialog = true,
        )
}
