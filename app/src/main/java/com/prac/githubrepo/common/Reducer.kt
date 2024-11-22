package com.prac.githubrepo.common

interface Reducer<Mutation, ViewState> {
    operator fun invoke(mutation: Mutation, currentState: ViewState): ViewState
}