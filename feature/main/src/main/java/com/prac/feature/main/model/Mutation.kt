package com.prac.feature.main.model

import com.prac.feature.main.refresh.RefreshState

sealed interface Mutation {
    data object ShowRepositories : Mutation
    data class ShowError(val errorMessage: String) : Mutation
    data class UpdateRefreshState(val refreshState: RefreshState) : Mutation
}