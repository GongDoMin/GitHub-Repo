package com.prac.githubrepo.common

import kotlinx.coroutines.flow.Flow

interface ActionProcessor<Action, UiState, Event> {
    operator fun invoke(action: Action): Flow<Pair<UiState?, Event?>>
}
