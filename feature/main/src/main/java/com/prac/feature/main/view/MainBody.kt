package com.prac.feature.main.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.prac.core.designsystem.R
import com.prac.data.model.RepoModel
import kotlinx.coroutines.flow.flowOf
import org.mockito.kotlin.description

@Composable
fun MainContentBody(
    repositories: LazyPagingItems<RepoModel>,
    handleLoadState: (CombinedLoadStates) -> LoadState,
    onClickRetry: () -> Unit,
    starStateRequest: (RepoModel) -> Unit,
    onClickStar: (RepoModel) -> Unit,
    onClickUnStar: (RepoModel) -> Unit,
    onClickRepository: (RepoModel) -> Unit
) {
    val lazyColumnDescription = stringResource(id = R.string.lazy_column_description)

    LazyColumn(
        modifier = Modifier
            .semantics { contentDescription = lazyColumnDescription }
    ) {
        items(
            count = repositories.itemCount,
            key = repositories.itemKey { it.id }
        ) { index ->
            repositories[index].let { repository ->
                repository?.let {
                    MainItem(
                        repository = repository,
                        onClickStar = onClickStar,
                        onClickUnStar = onClickUnStar,
                        onClickRepository = onClickRepository,
                        modifier = Modifier
                            .padding(
                                bottom = dimensionResource(id = R.dimen.padding_small)
                            )
                    )

                    if (repository.isStarred == null) starStateRequest(repository)
                }
            }
        }

        item {
            LoadStateFooter(
                loadState = handleLoadState(repositories.loadState),
                onRetryClick = onClickRetry
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentBodyPreview() {
    val repositories = flowOf(
        PagingData.from(
            listOf(
                RepoModel(stargazersCount = 5),
                RepoModel(stargazersCount = 6)
            )
        )
    ).collectAsLazyPagingItems()

    MainContentBody(
        repositories = repositories,
        handleLoadState = { LoadState.NotLoading(true) },
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MainContentBodyLoadStatePreview() {
    val repositories = flowOf(
        PagingData.from(
            listOf(
                RepoModel(stargazersCount = 5),
                RepoModel(stargazersCount = 6)
            )
        )
    ).collectAsLazyPagingItems()

    MainContentBody(
        repositories = repositories,
        handleLoadState = { LoadState.NotLoading(true) },
        onClickRetry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
    )
}