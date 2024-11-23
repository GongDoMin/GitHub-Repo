package com.prac.githubrepo.ui.home.main.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.components.ErrorAlertDialog

@Composable
fun MainContent(
    repositories: List<RepoEntity>,
    itemCount: Int,
    itemKey: ((Int) -> Any)?,
    loadState: CombinedLoadStates,
    retry: () -> Unit,
    handleLoadState: (CombinedLoadStates) -> LoadState?,
    starStateRequest: (RepoEntity) -> Unit,
    onClickStar: (RepoEntity) -> Unit,
    onClickUnStar: (RepoEntity) -> Unit,
    onClickRepository: (RepoEntity) -> Unit,
    dialogMessage: String,
    onDismissRequest: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainHeader()

        MainContentBody(
            repositories = repositories,
            itemCount = itemCount,
            itemKey = itemKey,
            loadState = loadState,
            retry = retry,
            handleLoadState = handleLoadState,
            starStateRequest = starStateRequest,
            onClickStar = onClickStar,
            onClickUnStar = onClickUnStar,
            onClickRepository = onClickRepository
        )

        if (dialogMessage.isNotEmpty()) {
            ErrorAlertDialog(
                onDismissRequest = onDismissRequest,
                errorMessage = dialogMessage
            )
        }
    }
}