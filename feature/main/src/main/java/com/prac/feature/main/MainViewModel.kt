package com.prac.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.prac.core.common.dispatcher.IODispatcher
import com.prac.core.common.mvi.action.ActionProcessor
import com.prac.core.common.mvi.model.model
import com.prac.core.common.mvi.reducer.Reducer
import com.prac.data.model.RepoModel
import com.prac.data.repository.RepoRepository
import com.prac.feature.main.di.MainActionAnnotation
import com.prac.feature.main.di.MainReducerAnnotation
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.model.Mutation
import com.prac.feature.main.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    @MainReducerAnnotation private val mainReducerProcessor: Reducer<Mutation, UiState>,
    @MainActionAnnotation private val mainActionProcessor: ActionProcessor<Action, Mutation, Event>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val model = model(
        reducerProcessor = mainReducerProcessor,
        actionProcessor = mainActionProcessor,
        initialState = UiState(),
        dispatcher = ioDispatcher
    )

    val uiStateFlow: StateFlow<UiState> = model.uiState
    val eventFlow: SharedFlow<Event> = model.event

    private val _repositories = MutableStateFlow<PagingData<RepoModel>>(PagingData.empty())
    val repositories = _repositories.asStateFlow()

    fun process(action: Action) {
        when (action) {
            is Action.InternalAction.Load -> load()
            else -> model.process(action)
        }
    }

    private fun load() {
        viewModelScope.launch {
            repoRepository.getRepositories().cachedIn(viewModelScope).collect { pagingData ->
                _repositories.update { pagingData }
            }
        }
    }

    fun handleLoadStates(combinedLoadStates: CombinedLoadStates) : LoadState {
        if (combinedLoadStates.refresh is LoadState.Error) {
            if ((combinedLoadStates.refresh as LoadState.Error).error !is IOException) {
                process(Action.InternalAction.Logout)
                return LoadState.NotLoading(true)
            }
            return combinedLoadStates.refresh
        }

        if (combinedLoadStates.refresh is LoadState.Loading) {
            return combinedLoadStates.refresh
        }

        if (combinedLoadStates.append is LoadState.Error) {
            if ((combinedLoadStates.append as LoadState.Error).error !is IOException) {
                process(Action.InternalAction.Logout)
                return LoadState.NotLoading(true)
            }
            return combinedLoadStates.append
        }

        return combinedLoadStates.append
    }

    init {
        process(Action.InternalAction.Load)
    }
}