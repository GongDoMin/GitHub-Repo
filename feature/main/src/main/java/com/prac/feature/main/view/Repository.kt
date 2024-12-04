package com.prac.feature.main.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import com.prac.core.common.constants.CONNECTION_FAIL
import com.prac.core.designsystem.R
import com.prac.core.designsystem.component.UserProfile
import com.prac.data.model.OwnerModel
import com.prac.data.model.RepoModel

@Composable
fun Repository(
    repository: RepoModel,
    onClickStar: (RepoModel) -> Unit,
    onClickUnStar: (RepoModel) -> Unit,
    onClickRepository: (RepoModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClickRepository(repository) }
            )
    ) {
        val itemModifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.padding_normal))
        val spacerModifier = Modifier
            .padding(top = dimensionResource(id = R.dimen.padding_small))

        Spacer(modifier = spacerModifier)

        MainItemUser(
            uri = repository.owner.avatarUrl,
            userName = repository.owner.login,
            modifier = itemModifier
        )

        Spacer(modifier = spacerModifier)

        MainItemRepoName(
            repoName = repository.name,
            modifier = itemModifier
        )

        Spacer(modifier = spacerModifier)

        MainItemStar(
            repo = repository,
            onClickStar = onClickStar,
            onClickUnStar = onClickUnStar,
            modifier = itemModifier
        )

        Spacer(modifier = spacerModifier)

        MainItemDefaultBranch(
            defaultBranch = repository.defaultBranch,
            modifier = itemModifier
        )

        Spacer(modifier = spacerModifier)

        MainItemUpdatedAt(
            updatedAt = repository.updatedAt,
            modifier = itemModifier
        )

        Spacer(modifier = spacerModifier)

        HorizontalDivider(modifier = itemModifier)
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
    repo: RepoModel,
    onClickStar: (RepoModel) -> Unit,
    onClickUnStar: (RepoModel) -> Unit,
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
                ),
            painter = painterResource(id = if (repo.isStarred == true) R.drawable.img_star else R.drawable.img_unstar),
            contentDescription =
                if (repo.isStarred == true) stringResource(id = R.string.star_image_description)
                else stringResource(id = R.string.unstar_image_description)
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
        RepoModel(
            id = 0,
            name = "test",
            owner = OwnerModel(
                login = "test",
                avatarUrl = ""
            ),
            stargazersCount = 0,
            defaultBranch = "test",
            updatedAt = "test",
            isStarred = false,
        )

    Repository(
        repository = repository,
        onClickStar = {},
        onClickUnStar = {},
        onClickRepository = {},
    )
}