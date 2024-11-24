package com.prac.githubrepo.ui.home.main.detail.model

import com.prac.data.entity.RepoDetailEntity

sealed interface Mutation {
    data class ShowRepository(val repository: RepoDetailEntity) : Mutation
    data object ShowLoading : Mutation
    data class ShowError(val errorMessage: String) : Mutation
    data object DismissError : Mutation
}