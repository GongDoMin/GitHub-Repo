package com.prac.githubrepo.ui.home.main.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prac.data.entity.RepoDetailEntity
import com.prac.githubrepo.R
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.INVALID_REPOSITORY
import com.prac.githubrepo.components.ErrorAlertDialog
import com.prac.githubrepo.components.LoadingContent
import com.prac.githubrepo.util.UserProfile
import com.prac.githubrepo.util.drawableID

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    DetailContent(
        isLoading = uiState.value is DetailViewModel.UiState.Loading,
        errorMessage = (uiState.value as? DetailViewModel.UiState.Error)?.errorMessage ?: "",
        repoDetail = (uiState.value as? DetailViewModel.UiState.Content)?.repository,
        onClickStar = viewModel::unStarRepository,
        onClickUnStar = viewModel::starRepository,
        onDismissRequest = { dialogMessage ->
            if (dialogMessage == CONNECTION_FAIL || dialogMessage == INVALID_REPOSITORY) onBack()
            else onNavigateToLogin()
        },
        modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_small))
    )
}

@Composable
fun DetailContent(
    isLoading: Boolean,
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

        if (errorMessage.isNotEmpty()) {
            ErrorAlertDialog(
                onDismissRequest = onDismissRequest,
                errorMessage = errorMessage
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
        UserProfile(uri = uri)

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
                )
                .semantics { drawableID = if (repoDetail.isStarred == true) R.drawable.img_star else R.drawable.img_unstar },
            painter = painterResource(id = if (repoDetail.isStarred == true) R.drawable.img_star else R.drawable.img_unstar),
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)),
            text = repoDetail.stargazersCount.toString()
        )

        Image(
            modifier = Modifier
                .size(20.dp)
                .padding(start = dimensionResource(id = R.dimen.padding_small))
                .semantics { drawableID = R.drawable.img_fork },
            painter = painterResource(id = R.drawable.img_fork),
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)),
            text = repoDetail.forksCount.toString()
        )
    }
}