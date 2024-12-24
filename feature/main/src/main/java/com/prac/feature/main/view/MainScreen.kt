package com.prac.feature.main.view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.prac.core.common.constants.INVALID_TOKEN
import com.prac.core.common.ui.MessageDialog
import com.prac.core.designsystem.R
import com.prac.feature.main.MainViewModel
import com.prac.feature.main.model.Action
import com.prac.feature.main.model.Event
import com.prac.feature.main.refresh.RefreshState

@Composable
internal fun MainScreen(
    onNavigateToLogin: () -> Unit,
    onClickRepository: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val repositories = remember { viewModel.repositories }.collectAsLazyPagingItems()
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = modifier
    ) {
        MainContent(
            repositories = repositories,
            loadState = viewModel.handleLoadStates(repositories.loadState),
            onClickRetry = { viewModel.process(Action.UserAction.OnClickRetry) },
            starStateRequest = { viewModel.process(Action.InternalAction.FetchStarState(it)) },
            onClickStar = { viewModel.process(Action.UserAction.OnClickStar(it)) },
            onClickUnStar = { viewModel.process(Action.UserAction.OnClickUnStar(it)) },
            onClickRepository = { onClickRepository(it.owner.login, it.name) },
            refreshState = (uiState.value as? UiState.Content)?.refreshState ?: RefreshState.Default,
            onUpdateRefreshState = { viewModel.process(Action.InternalAction.UpdateRefreshState(it))}
        )

        if (uiState.value is UiState.Error) {
            MessageDialog(
                onDismissRequest = { dialogMessage ->
                    if (dialogMessage == INVALID_TOKEN) viewModel.process(Action.UserAction.LogoutDialogDismiss)
                    else viewModel.process(Action.UserAction.DialogDismiss)
                },
                message = (uiState.value as UiState.Error).message,
                confirmButtonText = stringResource(id = R.string.check)
            )
        }
    }

    LaunchedEffect(repositories.loadState) {
        if (viewModel.handleLoadStates(repositories.loadState) == LoadState.Loading) {
            if (uiState.value is UiState.Content) {
                if ((uiState.value as UiState.Content).refreshState == RefreshState.Refreshing)
                    viewModel.process(Action.InternalAction.UpdateRefreshState(RefreshState.CompleteRefreshing))
            }
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collect {
                when (it) {
                    is Event.Retry -> repositories.retry()
                    is Event.OpenRepositoryDetail -> onClickRepository(it.userName, it.repoName)
                    is Event.Logout -> onNavigateToLogin()
                }
            }
        }
    }
}