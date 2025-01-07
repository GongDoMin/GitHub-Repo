package com.prac.feature.main.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.prac.core.designsystem.R
import com.prac.data.model.Repository
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun RepositoryList(
    repositories: LazyPagingItems<Repository>,
    loadState: LoadState,
    onClickRetry: () -> Unit,
    starStateRequest: (Repository) -> Unit,
    onClickStar: (Repository) -> Unit,
    onClickUnStar: (Repository) -> Unit,
    onClickRepository: (Repository) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val lazyColumnDescription = stringResource(id = R.string.lazy_column_description)

    LazyColumn(
        modifier = modifier
            .semantics { contentDescription = lazyColumnDescription }
    ) {
        items(
            count = repositories.itemCount,
            key = repositories.itemKey { it.id }
        ) { index ->
            repositories[index].let { repository ->
                repository?.let {
                    RepositoryItem(
                        repository = repository,
                        onClickStar = onClickStar,
                        onClickUnStar = onClickUnStar,
                        onClickRepository = onClickRepository,
                        contentPadding = contentPadding
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(
                            start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                            end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
                        )
                    )

                    if (repository.isStarred == null) starStateRequest(repository)
                }
            }
        }

        item {
            LoadStateFooter(
                loadState = loadState,
                onRetryClick = onClickRetry,
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.padding_small))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun RepositoryListPreview() {
    val repositories = flowOf(
        PagingData.from(
            listOf(
                Repository(stargazersCount = 5),
                Repository(stargazersCount = 6)
            )
        )
    ).collectAsLazyPagingItems()

    RepositoryList(
        repositories = repositories,
        loadState = LoadState.NotLoading(true),
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        contentPadding = PaddingValues()
    )
}

@Preview(showBackground = true)
@Composable
internal fun RepositoryListLoadStatePreview() {
    val repositories = flowOf(
        PagingData.from(
            listOf(
                Repository(stargazersCount = 5),
                Repository(stargazersCount = 6)
            )
        )
    ).collectAsLazyPagingItems()

    RepositoryList(
        repositories = repositories,
        loadState = LoadState.NotLoading(true),
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        contentPadding = PaddingValues()
    )
}