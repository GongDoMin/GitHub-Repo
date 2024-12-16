package com.prac.feature.main.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.LayoutDirection
import com.prac.core.designsystem.R
import com.prac.core.common.ui.UserProfile
import com.prac.data.model.OwnerModel
import com.prac.data.model.RepoModel

@Composable
fun Repository(
    repository: RepoModel,
    onClickStar: (RepoModel) -> Unit,
    onClickUnStar: (RepoModel) -> Unit,
    onClickRepository: (RepoModel) -> Unit,
    contentPadding: PaddingValues,
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
        val spacerModifier = Modifier
            .padding(top = dimensionResource(id = R.dimen.padding_small))
        val itemModifier = Modifier
            .padding(
                start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
            )

        Spacer(modifier = Modifier.padding(contentPadding.calculateTopPadding()))

        UserProfile(
            uri = repository.owner.avatarUrl,
            userName = repository.owner.login,
            modifier = itemModifier
        )

        Spacer(modifier = spacerModifier)

        RepositoryName(
            repoName = repository.name,
            modifier = itemModifier
        )

        Spacer(modifier = spacerModifier)

        RepositoryStarState(
            repo = repository,
            onClickStar = onClickStar,
            onClickUnStar = onClickUnStar,
            modifier = itemModifier
        )

        Spacer(modifier = spacerModifier)

        RepositoryBranch(
            defaultBranch = repository.defaultBranch,
            modifier = itemModifier
        )

        Spacer(modifier = spacerModifier)

        RepositoryUpdatedAt(
            updatedAt = repository.updatedAt,
            modifier = itemModifier
        )

        Spacer(modifier = Modifier.padding(contentPadding.calculateBottomPadding()))
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

        Spacer(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)))

        Text(text = userName)
    }
}

@Composable
fun RepositoryName(
    repoName: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = repoName
    )
}

@Composable
fun RepositoryStarState(
    repo: RepoModel,
    onClickStar: (RepoModel) -> Unit,
    onClickUnStar: (RepoModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (repo.isStarred == true) {
            StarImage(
                repo = repo,
                onClickStar = onClickStar,
                modifier = Modifier.size(dimensionResource(id = R.dimen.star))
            )
        } else {
            UnStarImage(
                repo = repo,
                onClickUnStar = onClickUnStar,
                modifier = Modifier.size(dimensionResource(id = R.dimen.star))
            )
        }

        Spacer(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)))

        Text(text = repo.stargazersCount.toString())
    }
}

@Composable
fun StarImage(
    repo: RepoModel,
    onClickStar: (RepoModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .clickable { onClickStar(repo) },
        painter = painterResource(id = R.drawable.img_star),
        contentDescription = stringResource(id = R.string.star_image_description)
    )
}

@Composable
fun UnStarImage(
    repo: RepoModel,
    onClickUnStar: (RepoModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        modifier = modifier
            .clickable { onClickUnStar(repo) },
        painter = painterResource(id = R.drawable.img_unstar),
        contentDescription = stringResource(id = R.string.unstar_image_description)
    )
}

@Composable
fun RepositoryBranch(
    defaultBranch: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = defaultBranch
    )
}

@Composable
fun RepositoryUpdatedAt(
    updatedAt: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = updatedAt
    )
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
        contentPadding = PaddingValues()
    )
}