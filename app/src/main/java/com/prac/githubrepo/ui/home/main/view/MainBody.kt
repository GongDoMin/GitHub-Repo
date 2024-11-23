package com.prac.githubrepo.ui.home.main.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.R

@Composable
fun MainContentBody(
    repositories: List<RepoEntity>,
    itemCount: Int,
    itemKey: ((Int) -> Any)?,
    loadState: CombinedLoadStates,
    retry: () -> Unit,
    handleLoadState: (CombinedLoadStates) -> LoadState?,
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
                loadState = handleLoadState(loadState),
                onRetryClick = retry
            )
        }
    }
}