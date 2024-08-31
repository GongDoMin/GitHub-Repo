package com.prac.githubrepo.main

import android.util.SparseArray
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.prac.data.entity.RepoEntity
import com.prac.data.exception.GitHubApiException
import com.prac.data.repository.RepoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repoRepository: RepoRepository,
    private val starStateMediator: StarStateMediator
): ViewModel() {
    sealed class UiState {
        data object Idle : UiState()

        data object Loading : UiState()

        data class ShowPagingData(
            val repositories : PagingData<RepoEntity>
        ) : UiState()
    }

    init {
        getRepositories()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _isStarredList = MutableStateFlow<List<Pair<Int, Boolean>>>(emptyList())

    private fun getRepositories() {
        viewModelScope.launch {
            if (_uiState.value != UiState.Idle) return@launch

            combine(
                repoRepository.getRepositories().cachedIn(viewModelScope),
                _isStarredList
            ) { pagingData, isStarredList ->
                isStarredList.fold(pagingData) { acc, pair ->
                    acc.map { repoEntity ->
                        if (repoEntity.id == pair.first) repoEntity.copy(isStarred = pair.second)
                        else repoEntity
                    }
                }
            }.collect { transformedPagingData ->
                _uiState.update { UiState.ShowPagingData(transformedPagingData) }
            }

        }
    }

    fun updateIsStarred(id: Int, isStarred: Boolean) {
        _isStarredList.update {
            it + Pair(id, isStarred)
        }
    }
}