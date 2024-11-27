package com.prac.feature.profile.model

sealed interface Mutation {
    data object ShowIdle : Mutation
    data object ShowLoading : Mutation
    data object ShowDialog : Mutation
}