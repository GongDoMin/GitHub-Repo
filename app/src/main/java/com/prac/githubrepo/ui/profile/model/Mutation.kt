package com.prac.githubrepo.ui.profile.model

sealed interface Mutation {
    data object ShowIdle : Mutation
    data object ShowLoading : Mutation
    data object ShowDialog : Mutation
}