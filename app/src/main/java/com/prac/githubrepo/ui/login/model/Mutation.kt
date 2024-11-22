package com.prac.githubrepo.ui.login.model

sealed interface Mutation {
    data object ShowIdle : Mutation
    data object ShowLoading : Mutation
    data class ShowError(val errorMessage: String) : Mutation
}