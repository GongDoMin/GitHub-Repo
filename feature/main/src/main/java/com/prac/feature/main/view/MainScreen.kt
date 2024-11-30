package com.prac.feature.main.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.feature.main.MainViewModel
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event

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
        repositories = repositories,
        handleLoadState = { viewModel.handleLoadStates(it) },
        onClickRetry = { viewModel.process(Action.UserAction.OnClickRetry) },
        starStateRequest = { viewModel.process(Action.InternalAction.FetchStarState(it)) },
        onClickStar = { viewModel.process(Action.UserAction.OnClickStar(it)) },
        onClickUnStar = { viewModel.process(Action.UserAction.OnClickUnStar(it)) },
        onClickRepository = { onClickRepository(it.owner.login, it.name) },
        isError = uiState.value.isError,
        errorMessage = uiState.value.errorMessage,
        onDismissRequest = { dialogMessage ->
            if (dialogMessage == INVALID_TOKEN) viewModel.process(Action.UserAction.LogoutDialogDismiss)
            else viewModel.process(Action.UserAction.DialogDismiss)
        }
    )

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect {
                when (it) {
                    is Event.Retry -> repositories.retry()
                    is Event.OpenRepositoryDetail -> onClickRepository(it.userName, it.repoName)
                    is Event.Logout -> viewModel.logout()
                }
            }
        }
    }
}