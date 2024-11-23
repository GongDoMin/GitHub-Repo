package com.prac.githubrepo.ui.home.main.view

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.prac.githubrepo.constants.INVALID_TOKEN
import com.prac.githubrepo.ui.home.main.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onClickRepository: (String, String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val repositories = uiState.value.repositories.collectAsLazyPagingItems()

    MainContent(
        repositories = repositories.itemSnapshotList.items,
        itemCount = repositories.itemCount,
        itemKey = repositories.itemKey { it.id },
        loadState = repositories.loadState,
        retry = repositories::retry,
        handleLoadState = viewModel::handleLoadStates,
        starStateRequest = viewModel::fetchStarState,
        onClickStar = viewModel::unStarRepository,
        onClickUnStar = viewModel::starRepository,
        onClickRepository = { onClickRepository(it.owner.login, it.name) },
        dialogMessage = uiState.value.dialogMessage,
        onDismissRequest = { dialogMessage -> if (dialogMessage == INVALID_TOKEN) onNavigateToLogin() }
    )
}