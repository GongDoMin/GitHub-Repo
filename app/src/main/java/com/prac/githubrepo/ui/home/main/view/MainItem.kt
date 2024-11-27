package com.prac.githubrepo.ui.home.main.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.prac.core.designsystem.component.UserProfile
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.R
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.githubrepo.util.drawableID

@Composable
fun MainItem(
    repository: RepoEntity,
    onClickStar: (RepoEntity) -> Unit,
    onClickUnStar: (RepoEntity) -> Unit,
    onClickRepository: (RepoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClickRepository(repository) }
            )
            .padding(
                top = dimensionResource(id = R.dimen.padding_normal),
                start = dimensionResource(id = R.dimen.padding_normal),
                end = dimensionResource(id = R.dimen.padding_normal)
            )
    ) {
        MainItemUser(
            uri = repository.owner.avatarUrl,
            userName = repository.owner.login,
            modifier = modifier
        )

        MainItemRepoName(
            repoName = repository.name,
            modifier = modifier
        )

        MainItemStar(
            repo = repository,
            onClickStar = onClickStar,
            onClickUnStar = onClickUnStar,
            modifier = modifier
        )

        MainItemDefaultBranch(
            defaultBranch = repository.defaultBranch,
            modifier = modifier
        )

        MainItemUpdatedAt(
            updatedAt = repository.updatedAt,
            modifier = modifier
        )

        HorizontalDivider()
    }
}

@Composable
fun MainItemUser(
    uri: String,
    userName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserProfile(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.user_profile))
                .clip(CircleShape),
            uri = uri
        )

        Text(
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.padding_small)),
            text = userName
        )
    }
}

@Composable
fun MainItemRepoName(
    repoName: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = repoName
    )
}

@Composable
fun MainItemStar(
    repo: RepoEntity,
    onClickStar: (RepoEntity) -> Unit,
    onClickUnStar: (RepoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(dimensionResource(R.dimen.star))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (repo.isStarred == true) onClickStar(repo)
                        else onClickUnStar(repo)
                    }
                )
                .semantics {
                    drawableID =
                        if (repo.isStarred == true) R.drawable.img_star else R.drawable.img_unstar
                },
            painter = painterResource(id = if (repo.isStarred == true) R.drawable.img_star else R.drawable.img_unstar),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.padding_small))
            ,
            text = repo.stargazersCount.toString()
        )
    }
}

@Composable
fun MainItemDefaultBranch(
    defaultBranch: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = defaultBranch
    )
}

@Composable
fun MainItemUpdatedAt(
    updatedAt: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = updatedAt
    )
}

@Composable
fun LoadStateFooter(
    loadState: LoadState?,
    onRetryClick: () -> Unit
) {
    when (loadState) {
        is LoadState.Loading -> LoadingFooter()

        is LoadState.Error -> LoadErrorFooter(onRetryClick = onRetryClick)

        else -> { }
    }
}

@Composable
fun LoadingFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.progressbar)),
            trackColor = Color.Transparent,
            color = Color.Black
        )
    }
}

@Composable
fun LoadErrorFooter(
    onRetryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = CONNECTION_FAIL,
            color = Color.Black,
            modifier = Modifier
                .padding(
                    bottom = dimensionResource(id = R.dimen.padding_small)
                )
        )

        Button(
            onClick = onRetryClick,
            colors = ButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            )
        ) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainItemPreview() {
    val repository =
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

    MainItem(
        repository = repository,
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
        modifier = Modifier
            .padding(bottom = 8.dp)
    )
}