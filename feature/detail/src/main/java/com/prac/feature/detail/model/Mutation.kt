package com.prac.feature.detail.model

import com.prac.data.model.RepoDetailModel

sealed interface Mutation {
    data class ShowRepository(val repository: RepoDetailModel) : Mutation
    data object ShowLoading : Mutation
    data class ShowError(val errorMessage: String) : Mutation
}