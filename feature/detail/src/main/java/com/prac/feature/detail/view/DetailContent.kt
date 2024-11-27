package com.prac.feature.detail.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.designsystem.R
import com.prac.core.designsystem.component.ErrorAlertDialog
import com.prac.core.designsystem.component.LoadingContent
import com.prac.core.designsystem.component.UserProfile
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoDetailEntity

@Composable
fun DetailContent(
    isLoading: Boolean,
    isError: Boolean,
    errorMessage: String,
    repoDetail: RepoDetailEntity?,
    onClickStar: (RepoDetailEntity) -> Unit,
    onClickUnStar: (RepoDetailEntity) -> Unit,
    onDismissRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LoadingContent(
        isLoading = isLoading
    ) {
        repoDetail?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.padding_normal))
            ) {
                DetailContentUser(
                    uri = repoDetail.owner.avatarUrl,
                    userName = repoDetail.owner.login,
                    modifier = modifier
                )

                DetailContentRepoName(
                    repoName = repoDetail.name,
                    modifier = Modifier
                        .padding(bottom = dimensionResource(id = R.dimen.padding_normal))
                )

                DetailContentStarAndFork(
                    repoDetail = repoDetail,
                    modifier = modifier,
                    onClickStar = onClickStar,
                    onClickUnStar = onClickUnStar
                )
            }
        }

        if (isError) {
            ErrorAlertDialog(
                onDismissRequest = onDismissRequest,
                errorMessage = errorMessage,
                confirmButtonText = stringResource(id = R.string.check)
            )
        }
    }
}

@Composable
fun DetailContentUser(
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
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)),
            text = userName
        )
    }
}

@Composable
fun DetailContentRepoName(
    repoName: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = repoName,
        fontSize = 32.sp
    )
}

@Composable
fun DetailContentStarAndFork(
    repoDetail: RepoDetailEntity,
    modifier: Modifier,
    onClickStar: (RepoDetailEntity) -> Unit,
    onClickUnStar: (RepoDetailEntity) -> Unit
) {
    Row(
        modifier = modifier,
    ) {
        Image(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.star))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (repoDetail.isStarred == true) onClickStar(repoDetail)
                        else onClickUnStar(repoDetail)
                    }
                ),
            painter = painterResource(id = if (repoDetail.isStarred == true) R.drawable.img_star else R.drawable.img_unstar),
            contentDescription =
                if (repoDetail.isStarred == true) stringResource(id = R.string.star_image_description)
                else stringResource(id = R.string.unstar_image_description)
        )

        Text(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)),
            text = repoDetail.stargazersCount.toString()
        )

        Image(
            modifier = Modifier
                .size(20.dp)
                .padding(start = dimensionResource(id = R.dimen.padding_small)),
            painter = painterResource(id = R.drawable.img_fork),
            contentDescription = stringResource(id = R.string.fork_image_description)
        )

        Text(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)),
            text = repoDetail.forksCount.toString()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailContentPreview() {
    val repository = RepoDetailEntity(
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

    DetailContent(
        isLoading = false,
        isError = false,
        errorMessage = "",
        repoDetail = repository,
        onClickStar = {},
        onClickUnStar = {},
        onDismissRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
fun DetailContentLoadingPreview() {
    DetailContent(
        isLoading = true,
        isError = false,
        errorMessage = "",
        repoDetail = RepoDetailEntity(),
        onClickStar = {},
        onClickUnStar = {},
        onDismissRequest = {}
    )
}

@Preview(showBackground = true)
@Composable
fun DetailContentErrorPreview() {
    DetailContent(
        isLoading = false,
        isError = true,
        errorMessage = INVALID_REPOSITORY,
        repoDetail = RepoDetailEntity(),
        onClickStar = {},
        onClickUnStar = {},
        onDismissRequest = {}
    )
}