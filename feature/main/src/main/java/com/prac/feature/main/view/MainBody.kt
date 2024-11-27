package com.prac.feature.main.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import com.prac.core.designsystem.R
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoEntity
import java.io.IOException

@Composable
fun MainContentBody(
    repositories: List<RepoEntity>,
    itemCount: Int,
    itemKey: ((Int) -> Any)?,
    loadState: LoadState,
    retry: () -> Unit,
    starStateRequest: (RepoEntity) -> Unit,
    onClickStar: (RepoEntity) -> Unit,
    onClickUnStar: (RepoEntity) -> Unit,
    onClickRepository: (RepoEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .testTag("lazyColumn")
    ) {
        items(
            count = itemCount,
            key = itemKey
        ) { index ->
            repositories[index].let { repository ->
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

        item {
            LoadStateFooter(
                loadState = loadState,
                onRetryClick = retry
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentBodyPreview() {
    val repositories = listOf(
        RepoEntity(
            id = 0,
            name = "test",
            owner = OwnerEntity(
                login = "test",
                avatarUrl = ""
            ),
            stargazersCount = 0,
            defaultBranch = "test",
            updatedAt = "test",
            isStarred = false,
        )
    )

    MainContentBody(
        repositories = repositories,
        itemCount = repositories.size,
        itemKey = { repositories[it].id },
        loadState = LoadState.NotLoading(true),
        retry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MainContentBodyLoadStatePreview() {
    val repositories = listOf(
        RepoEntity(
            id = 0,
            name = "test",
            owner = OwnerEntity(
                login = "test",
                avatarUrl = ""
            ),
            stargazersCount = 0,
            defaultBranch = "test",
            updatedAt = "test",
            isStarred = false,
        )
    )

    MainContentBody(
        repositories = repositories,
        itemCount = repositories.size,
        itemKey = { repositories[it].id },
        loadState = LoadState.Error(IOException()),
        retry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
    )
}