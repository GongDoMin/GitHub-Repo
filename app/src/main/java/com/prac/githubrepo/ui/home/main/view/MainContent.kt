package com.prac.githubrepo.ui.home.main.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.components.ErrorAlertDialog
import com.prac.githubrepo.constants.INVALID_TOKEN
import java.io.IOException

@Composable
fun MainContent(
    repositories: List<RepoEntity>,
    itemCount: Int,
    itemKey: ((Int) -> Any)?,
    loadState: LoadState,
    retry: () -> Unit,
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
            itemCount = itemCount,
            itemKey = itemKey,
            loadState = loadState,
            retry = retry,
            starStateRequest = starStateRequest,
            onClickStar = onClickStar,
            onClickUnStar = onClickUnStar,
            onClickRepository = onClickRepository
        )

        if (isError) {
            ErrorAlertDialog(
                onDismissRequest = onDismissRequest,
                errorMessage = errorMessage
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
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

    MainContent(
        repositories = repositories,
        itemCount = repositories.size,
        itemKey = { repositories[it].id },
        loadState = LoadState.NotLoading(true),
        retry = {},
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

    MainContent(
        repositories = repositories,
        itemCount = repositories.size,
        itemKey = { repositories[it].id },
        loadState = LoadState.NotLoading(true),
        retry = {},
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

    MainContent(
        repositories = repositories,
        itemCount = repositories.size,
        itemKey = { repositories[it].id },
        loadState = LoadState.Error(IOException()),
        retry = {},
        starStateRequest = {},
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        isError = false,
        errorMessage = "",
        onDismissRequest = {}
    )
}