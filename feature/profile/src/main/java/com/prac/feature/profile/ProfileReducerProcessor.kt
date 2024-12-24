package com.prac.feature.profile

import com.prac.core.common.mvi.reducer.Reducer
import com.prac.feature.profile.model.Mutation
import com.prac.feature.profile.view.UiState

internal class ProfileReducerProcessor : Reducer<Mutation, UiState> {
    override fun invoke(mutation: Mutation, currentState: UiState): UiState =
        when (mutation) {
            is Mutation.ShowIdle -> showIdle()
            is Mutation.ShowLoading -> showLoading()
            is Mutation.ShowDialog -> showDialog()
        }

    private fun showIdle() =
        UiState.Idle

    private fun showLoading() =
        UiState.Loading

    private fun showDialog() =
        UiState.Dialog
}