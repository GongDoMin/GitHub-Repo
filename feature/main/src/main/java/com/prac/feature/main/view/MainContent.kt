package com.prac.feature.main.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.prac.core.designsystem.R
import com.prac.data.model.Repository
import com.prac.feature.main.refresh.PullToRefreshLayout
import com.prac.feature.main.refresh.RefreshState
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun MainContent(
    repositories: LazyPagingItems<Repository>,
    loadState: LoadState,
    onClickRetry: () -> Unit,
    starStateRequest: (Repository) -> Unit,
    onClickStar: (Repository) -> Unit,
    onClickUnStar: (Repository) -> Unit,
    onClickRepository: (Repository) -> Unit,
    refreshState: RefreshState,
    onUpdateRefreshState: (RefreshState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)))

        MainTitle()

        Spacer(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.padding_small)))

        HorizontalDivider()

        PullToRefreshLayout(
            refreshState = refreshState,
            onUpdateRefreshState = onUpdateRefreshState,
            onRefresh = repositories::refresh
        ) {
            RepositoryList(
                repositories = repositories,
                loadState = loadState,
                onClickRetry = onClickRetry,
                starStateRequest = starStateRequest,
                onClickStar = onClickStar,
                onClickUnStar = onClickUnStar,
                onClickRepository = onClickRepository,
                contentPadding = PaddingValues(
                    vertical = dimensionResource(id = R.dimen.padding_small),
                    horizontal = dimensionResource(id = R.dimen.padding_normal)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun MainContentPreview() {
    val repositories = flowOf(
        PagingData.from(
            listOf(
                Repository(stargazersCount = 5),
                Repository(stargazersCount = 6)
            )
        )
    ).collectAsLazyPagingItems()

    MainContent(
        repositories = repositories,
        loadState = LoadState.NotLoading(true),
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        refreshState = RefreshState.Default,
        onUpdateRefreshState = {}
    )
}

@Preview(showBackground = true)
@Composable
internal fun MainContentErrorPreview() {
    val repositories = flowOf(
        PagingData.from(
            listOf(
                Repository(stargazersCount = 5),
                Repository(stargazersCount = 6)
            )
        )
    ).collectAsLazyPagingItems()

    MainContent(
        repositories = repositories,
        loadState = LoadState.NotLoading(true),
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        refreshState = RefreshState.Default,
        onUpdateRefreshState = {}
    )
}

@Preview(showBackground = true)
@Composable
internal fun MainContentLoadStatePreview() {
    val repositories = flowOf<PagingData<Repository>>(PagingData.empty()).collectAsLazyPagingItems()

    MainContent(
        repositories = repositories,
        loadState = LoadState.Loading,
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        refreshState = RefreshState.Default,
        onUpdateRefreshState = {}
    )
}

@Preview(showBackground = true)
@Composable
internal fun MainContentRefreshStatePreview() {
    val repositories = flowOf<PagingData<Repository>>(PagingData.empty()).collectAsLazyPagingItems()

    MainContent(
        repositories = repositories,
        loadState = LoadState.Loading,
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        refreshState = RefreshState.Refreshing,
        onUpdateRefreshState = {}
    )
}