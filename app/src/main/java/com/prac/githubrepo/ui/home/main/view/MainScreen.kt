package com.prac.githubrepo.ui.home.main.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.constants.INVALID_TOKEN
import com.prac.githubrepo.ui.home.main.MainViewModel
import com.prac.githubrepo.ui.home.main.model.Action
import com.prac.githubrepo.ui.home.main.model.Event

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onClickRepository: (String, String) -> Unit
) {
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val repositories = remember { viewModel.repositories }.collectAsLazyPagingItems()
    val lifecycleOwner = LocalLifecycleOwner.current

    MainContent(
        repositories = uiState.value.repositories,
        itemCount = uiState.value.repositories.size,
        itemKey = { uiState.value.repositories[it].id },
        loadState = uiState.value.loadState,
        retry = { viewModel.process(Action.UserAction.OnClickRetry) },
        starStateRequest = { viewModel.process(Action.InternalAction.FetchStarState(it)) },
        onClickStar = { viewModel.process(Action.UserAction.OnClickStar(it)) },
        onClickUnStar = { viewModel.process(Action.UserAction.OnClickUnStar(it)) },
        onClickRepository = { onClickRepository(it.owner.login, it.name) },
        isError = uiState.value.isError,
        errorMessage = uiState.value.errorMessage,
        onDismissRequest = { dialogMessage -> if (dialogMessage == INVALID_TOKEN) onNavigateToLogin() }
    )

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect {
                when (it) {
                    is Event.Reload -> repositories.retry()
                    is Event.OpenRepositoryDetail -> onClickRepository(it.userName, it.repoName)
                    is Event.Logout -> viewModel.logout()
                }
            }
        }
    }

    HandleRepositoryUpdate(
        repositories = repositories,
        handleCombineLoadState = viewModel::handleLoadStates,
        process = { list, loadState ->
            viewModel.process(
                action = Action.InternalAction.UpdateRepositories(
                    repositories = list,
                    loadState = loadState
                )
            )
        }
    )
}

@Composable
private fun HandleRepositoryUpdate(
    repositories: LazyPagingItems<RepoEntity>,
    handleCombineLoadState: (CombinedLoadStates) -> LoadState,
    process: (List<RepoEntity>, LoadState) -> Unit
) {
    LaunchedEffect(repositories.itemSnapshotList, repositories.loadState) {
        process(repositories.itemSnapshotList.items, handleCombineLoadState(repositories.loadState))
    }
}