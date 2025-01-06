package com.prac.feature.detail.view

import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prac.core.common.ui.DrawableImage
import com.prac.core.common.ui.UserProfile
import com.prac.core.designsystem.R
import com.prac.feature.detail.model.RepositoryDetail
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
internal fun DetailContent(
    repoDetail: RepositoryDetail,
    onClickStar: (RepositoryDetail) -> Unit,
    onClickUnStar: (RepositoryDetail) -> Unit,
    modifier: Modifier = Modifier,
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
}

@Composable
internal fun UserProfile(
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
internal fun RepositoryName(
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
internal fun RepositoryStarStateAndFork(
    repoDetail: RepositoryDetail,
    onClickStar: (RepositoryDetail) -> Unit,
    onClickUnStar: (RepositoryDetail) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Crossfade(
            targetState = repoDetail.isStarred,
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (repoDetail.isStarred == true) onClickStar(repoDetail)
                        else onClickUnStar(repoDetail)
                    }
                ),
            label = "star image crossfade"
        ) { targetState ->
            when (targetState) {
                true -> {
                    DrawableImage(
                        res = R.drawable.img_star,
                        contentDescription = stringResource(id = R.string.star_image_description),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.star))
                    )
                }
                else -> {
                    DrawableImage(
                        res = R.drawable.img_unstar,
                        contentDescription = stringResource(id = R.string.unstar_image_description),
                        modifier = Modifier.size(dimensionResource(id = R.dimen.star))
                    )
                }
            }
        }

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

        DrawableImage(
            res = R.drawable.img_fork,
            contentDescription = stringResource(id = R.string.fork_image_description),
            modifier = Modifier.size(20.dp)
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
internal fun RepositoryInformation(
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