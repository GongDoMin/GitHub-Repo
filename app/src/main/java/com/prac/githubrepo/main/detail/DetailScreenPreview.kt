package com.prac.githubrepo.main.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoDetailEntity
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.INVALID_REPOSITORY

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    DetailContent(
        isLoading = false,
        errorMessage = "",
        repoDetail = makeRepoDetail(),
        onClickStar = { },
        onClickUnStar = { },
        onDismissRequest = { }
    )
}

@Preview(showBackground = true)
@Composable
fun DetailScreenLoadingPreview() {
    DetailContent(
        isLoading = true,
        errorMessage = "",
        repoDetail = null,
        onClickStar = { },
        onClickUnStar = { },
        onDismissRequest = { }
    )
}

@Preview(showBackground = true)
@Composable
fun DetailScreenNotFoundRepositoryErrorPreview() {
    DetailContent(
        isLoading = true,
        errorMessage = INVALID_REPOSITORY,
        repoDetail = makeRepoDetail(),
        onClickStar = { },
        onClickUnStar = { },
        onDismissRequest = { }
    )
}

@Preview(showBackground = true)
@Composable
fun DetailScreenNetworkErrorPreview() {
    DetailContent(
        isLoading = true,
        errorMessage = CONNECTION_FAIL,
        repoDetail = makeRepoDetail(),
        onClickStar = { },
        onClickUnStar = { },
        onDismissRequest = { }
    )
}

private fun makeRepoDetail() =
    RepoDetailEntity(
        id = 1,
        name = "test1",
        owner = OwnerEntity(
            login = "test1",
            avatarUrl = "test1"
        ),
        stargazersCount = 10,
        forksCount = 10,
        isStarred = false
    )