package com.prac.feature.main.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.prac.core.designsystem.component.ErrorAlertDialog
import com.prac.data.entity.RepoEntity
import com.prac.core.designsystem.R
import com.prac.core.common.constants.INVALID_TOKEN
import kotlinx.coroutines.flow.flowOf

@Composable
fun MainContent(
    repositories: LazyPagingItems<RepoEntity>,
    handleLoadState: (CombinedLoadStates) -> LoadState,
    onClickRetry: () -> Unit,
    starStateRequest: (RepoEntity) -> Unit,
    onClickStar: (RepoEntity) -> Unit,
    onClickUnStar: (RepoEntity) -> Unit,
    onClickRepository: (RepoEntity) -> Unit,
    isError: Boolean,
    errorMessage: String,
    onDismissRequest: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MainHeader()

        HorizontalDivider()

        MainContentBody(
            repositories = repositories,
            handleLoadState = handleLoadState,
            onClickRetry = onClickRetry,
            starStateRequest = starStateRequest,
            onClickStar = onClickStar,
            onClickUnStar = onClickUnStar,
            onClickRepository = onClickRepository
        )

        if (isError) {
            ErrorAlertDialog(
                onDismissRequest = onDismissRequest,
                errorMessage = errorMessage,
                confirmButtonText = stringResource(id = R.string.check)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    val repositories = flowOf(PagingData.from(
            listOf(
                RepoEntity(stargazersCount = 5),
                RepoEntity(stargazersCount = 6)
            )
        )
    ).collectAsLazyPagingItems()

    MainContent(
        repositories = repositories,
        handleLoadState = { LoadState.NotLoading(true) },
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        isError = false,
        errorMessage = "",
        onDismissRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MainContentErrorPreview() {
    val repositories = flowOf(PagingData.from(
            listOf(
                RepoEntity(stargazersCount = 5),
                RepoEntity(stargazersCount = 6)
            )
        )
    ).collectAsLazyPagingItems()

    MainContent(
        repositories = repositories,
        handleLoadState = { LoadState.NotLoading(true) },
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        isError = true,
        errorMessage = INVALID_TOKEN,
        onDismissRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MainContentLoadStatePreview() {
    val repositories = flowOf(PagingData.from(
        listOf(
            RepoEntity(stargazersCount = 5),
            RepoEntity(stargazersCount = 6)
        )
    )
    ).collectAsLazyPagingItems()

    MainContent(
        repositories = repositories,
        handleLoadState = { LoadState.NotLoading(true) },
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        isError = false,
        errorMessage = "",
        onDismissRequest = {}
    )
}