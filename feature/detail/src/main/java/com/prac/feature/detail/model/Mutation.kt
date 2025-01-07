package com.prac.feature.detail.model

import com.prac.data.model.RepositoryDetail

sealed interface Mutation {
    data class ShowRepository(val repository: RepositoryDetail) : Mutation
    data object ShowLoading : Mutation
    data class ShowError(val errorMessage: String) : Mutation
}