package com.prac.feature.detail.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prac.core.common.constants.INVALID_REPOSITORY
import com.prac.core.designsystem.R
import com.prac.core.common.ui.MessageDialog
import com.prac.core.designsystem.component.ContentWithLoadingIndicator
import com.prac.core.common.ui.UserProfile
import com.prac.data.model.RepoDetailModel
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun DetailContent(
    isLoading: Boolean,
    isError: Boolean,
    errorMessage: String,
    repoDetail: RepoDetailModel,
    onClickStar: (RepoDetailModel) -> Unit,
    onClickUnStar: (RepoDetailModel) -> Unit,
    onDismissRequest: (String) -> Unit,
    modifier: Modifier = Modifier,
    loadingModifier: Modifier = Modifier
) {
    ContentWithLoadingIndicator(
        isLoading = isLoading,
        modifier = loadingModifier
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val spacerModifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.padding_normal))

            UserProfile(
                uri = repoDetail.owner.avatarUrl,
                userName = repoDetail.owner.login
            )

            Spacer(modifier = spacerModifier)

            RepositoryName(
                repoName = repoDetail.name
            )

            Spacer(modifier = spacerModifier)

            RepositoryStarStateAndFork(
                repoDetail = repoDetail,
                onClickStar = onClickStar,
                onClickUnStar = onClickUnStar
            )

            RepositoryInformation(
                imageVector = ImageVector.vectorResource(id = R.drawable.issue_24),
                title = stringResource(id = R.string.issue),
                value = repoDetail.issueCount,
                contentPadding = PaddingValues(vertical = dimensionResource(id = R.dimen.padding_normal))
            )

            RepositoryInformation(
                imageVector = ImageVector.vectorResource(id = R.drawable.pull_request_24),
                title = stringResource(id = R.string.pull_request),
                value = repoDetail.pullCount,
                contentPadding = PaddingValues(vertical = dimensionResource(id = R.dimen.padding_normal))
            )

            RepositoryInformation(
                imageVector = ImageVector.vectorResource(id = R.drawable.subscribe_24),
                title = stringResource(id = R.string.subscribe),
                value = repoDetail.subscribeCount,
                contentPadding = PaddingValues(vertical = dimensionResource(id = R.dimen.padding_normal))
            )

            if (repoDetail.readme.isNotEmpty()) {
                Spacer(modifier = spacerModifier)

                Text(
                    text = stringResource(id = R.string.readme)
                )

                Spacer(modifier = spacerModifier)

                HorizontalDivider()

                Spacer(modifier = spacerModifier)

                MarkdownText(
                    markdown = repoDetail.readme
                )
            }
        }

        if (isError) {
            MessageDialog(
                onDismissRequest = onDismissRequest,
                message = errorMessage,
                confirmButtonText = stringResource(id = R.string.check)
            )
        }
    }
}

@Composable
fun UserProfile(
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
fun RepositoryName(
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
fun RepositoryStarStateAndFork(
    repoDetail: RepoDetailModel,
    onClickStar: (RepoDetailModel) -> Unit,
    onClickUnStar: (RepoDetailModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
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

        Spacer(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)))

        Text(
            modifier = Modifier
                .drawBehind {
                    val strokeWidthPx = 1.dp.toPx()
                    val verticalOffset = size.height - 1.sp.toPx()
                    drawLine(
                        color = Color.Black,
                        strokeWidth = strokeWidthPx,
                        start = Offset(0f, verticalOffset),
                        end = Offset(size.width, verticalOffset)
                    )
                }
            ,
            text = stringResource(id = R.string.star_count, repoDetail.stargazersCount)
        )

        Spacer(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)))

        Image(
            modifier = Modifier
                .size(20.dp),
            painter = painterResource(id = R.drawable.img_fork),
            contentDescription = stringResource(id = R.string.fork_image_description)
        )

        Spacer(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)))

        Text(
            modifier = Modifier
                .drawBehind {
                    val strokeWidthPx = 1.dp.toPx()
                    val verticalOffset = size.height - 1.sp.toPx()
                    drawLine(
                        color = Color.Black,
                        strokeWidth = strokeWidthPx,
                        start = Offset(0f, verticalOffset),
                        end = Offset(size.width, verticalOffset)
                    )
                }
            ,
            text = stringResource(id = R.string.fork_count, repoDetail.forksCount)
        )
    }
}

@Composable
fun RepositoryInformation(
    imageVector: ImageVector,
    title: String,
    value: Int,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.info_icon))
                    .padding(end = dimensionResource(id = R.dimen.padding_small)),
                imageVector = imageVector,
                contentDescription = title
            )

            Text(
                text = title
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = value.toString()
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(
                    start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)))
    }
}

@Preview(showBackground = true)
@Composable
fun DetailContentPreview() {
    DetailContent(
        isLoading = false,
        isError = false,
        errorMessage = "",
        repoDetail = RepoDetailModel(readme = "hi!!"),
        onClickStar = {},
        onClickUnStar = {},
        onDismissRequest = {},
        modifier = Modifier
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun DetailContentLoadingPreview() {
    DetailContent(
        isLoading = true,
        isError = false,
        errorMessage = "",
        repoDetail = RepoDetailModel(),
        onClickStar = {},
        onClickUnStar = {},
        onDismissRequest = {},
        modifier = Modifier
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun DetailContentErrorPreview() {
    DetailContent(
        isLoading = false,
        isError = true,
        errorMessage = INVALID_REPOSITORY,
        repoDetail = RepoDetailModel(readme = "hi!!"),
        onClickStar = {},
        onClickUnStar = {},
        onDismissRequest = {},
        modifier = Modifier
            .padding(16.dp)
    )
}