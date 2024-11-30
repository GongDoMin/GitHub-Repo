package com.prac.feature.main.model

sealed interface Mutation {
    data object ShowRepositories : Mutation
    data class ShowError(val errorMessage: String) : Mutation
}