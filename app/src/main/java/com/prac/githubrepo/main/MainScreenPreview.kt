package com.prac.githubrepo.main

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.R
import com.prac.githubrepo.constants.INVALID_REPOSITORY
import com.prac.githubrepo.constants.INVALID_TOKEN
import kotlinx.coroutines.flow.flow
import java.io.IOException

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val content = MainViewModel.Content(
        repositories = flow { emit(PagingData.from(makeRepoEntity())) },
        dialogMessage = ""
    )

    MainContent(
        content = content,
        handleLoadState = { null },
        starStateRequest = { },
        onClickStar = { },
        onClickUnStar = { },
        onClickRepository = { },
        onDismissRequest = { }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenNetworkFailurePreview() {
    val content = MainViewModel.Content(
        repositories = flow { emit(PagingData.from(makeRepoEntity())) },
        dialogMessage = ""
    )

    MainContent(
        content = content,
        handleLoadState = { LoadState.Error(IOException()) },
        starStateRequest = { },
        onClickStar = { },
        onClickUnStar = { },
        onClickRepository = { },
        onDismissRequest = { }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenAuthorizationErrorPreview() {
    val content = MainViewModel.Content(
        repositories = flow { emit(PagingData.from(makeRepoEntity())) },
        dialogMessage = INVALID_TOKEN
    )

    MainContent(
        content = content,
        handleLoadState = { null },
        starStateRequest = { },
        onClickStar = { },
        onClickUnStar = { },
        onClickRepository = { },
        onDismissRequest = { }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenNotFoundRepositoryErrorPreview() {
    val content = MainViewModel.Content(
        repositories = flow { emit(PagingData.from(makeRepoEntity())) },
        dialogMessage = INVALID_REPOSITORY
    )

    MainContent(
        content = content,
        handleLoadState = { null },
        starStateRequest = { },
        onClickStar = { },
        onClickUnStar = { },
        onClickRepository = { },
        onDismissRequest = { }
    )
}

@Preview(showBackground = true)
@Composable
fun MainItemPreview() {
    val repository = RepoEntity(
        id = 0,
        name = "test 0",
        owner = OwnerEntity(
            login = "login 0",
            avatarUrl = "avatarUrl 0"
        ),
        defaultBranch = "defaultBranch 0",
        stargazersCount = 0,
        isStarred = false,
        updatedAt = "updatedAt"
    )
    MainContentItem(
        repository = repository,
        onClickStar = { },
        onClickUnStar = { },
        onClickRepository = { },
        modifier = Modifier
            .padding(
                bottom = dimensionResource(id = R.dimen.padding_small)
            )
    )
}

private fun makeRepoEntity() = listOf(
    RepoEntity(
        id = 0,
        name = "test 0",
        owner = OwnerEntity(
            login = "login 0",
            avatarUrl = "avatarUrl 0"
        ),
        defaultBranch = "defaultBranch 0",
        stargazersCount = 0,
        isStarred = true,
        updatedAt = "updatedAt"
    ),
    RepoEntity(
        id = 1,
        name = "test 1",
        owner = OwnerEntity(
            login = "login 1",
            avatarUrl = "avatarUrl 1"
        ),
        defaultBranch = "defaultBranch 1",
        stargazersCount = 1,
        isStarred = false,
        updatedAt = "updatedAt"
    )
)