package com.prac.githubrepo.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.R
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.INVALID_TOKEN
import com.prac.githubrepo.util.ErrorAlertDialog
import com.prac.githubrepo.util.UserProfile
import com.prac.githubrepo.util.drawableID

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onClickRepository: (String, String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    MainContent(
        uiState = uiState.value,
        handleLoadState = viewModel::handleLoadStates,
        starStateRequest = viewModel::fetchStarState,
        onClickStar = viewModel::unStarRepository,
        onClickUnStar = viewModel::starRepository,
        onClickRepository = { onClickRepository(it.owner.login, it.name) },
        onDismissRequest = { dialogMessage -> if (dialogMessage == INVALID_TOKEN) onLogout() }
    )
}

@Composable
fun MainContent(
    uiState: MainViewModel.UiState,
    handleLoadState: (CombinedLoadStates) -> LoadState?,
    starStateRequest: (RepoEntity) -> Unit,
    onClickStar: (RepoEntity) -> Unit,
    onClickUnStar: (RepoEntity) -> Unit,
    onClickRepository: (RepoEntity) -> Unit,
    onDismissRequest: (String) -> Unit
) {
    val repositories = uiState.repositories.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainContentTitle()

        MainContentBody(
            repositories = repositories,
            handleLoadState = handleLoadState,
            starStateRequest = starStateRequest,
            onClickStar = onClickStar,
            onClickUnStar = onClickUnStar,
            onClickRepository = onClickRepository
        )

        if (uiState.dialogMessage.isNotEmpty()) {
            ErrorAlertDialog(
                onDismissRequest = onDismissRequest,
                errorMessage = uiState.dialogMessage
            )
        }
    }
}

@Composable
fun MainContentTitle() {
    Text(
        modifier = Modifier
            .padding(
                top = dimensionResource(id = R.dimen.padding_normal)
            ),
        text = stringResource(id = R.string.repository)
    )
}

@Composable
fun MainContentBody(
    repositories: LazyPagingItems<RepoEntity>,
    handleLoadState: (CombinedLoadStates) -> LoadState?,
    starStateRequest: (RepoEntity) -> Unit,
    onClickStar: (RepoEntity) -> Unit,
    onClickUnStar: (RepoEntity) -> Unit,
    onClickRepository: (RepoEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .testTag("lazyColumn")
    ) {
        items(
            count = repositories.itemCount,
            key = repositories.itemKey { it.id }
        ) { index ->
            repositories[index]?.let { repository ->
                MainContentItem(
                    repository = repository,
                    onClickStar = onClickStar,
                    onClickUnStar = onClickUnStar,
                    onClickRepository = onClickRepository,
                    modifier = Modifier
                        .padding(
                            bottom = dimensionResource(id = R.dimen.padding_small)
                        )
                )

                if (repository.isStarred == null) starStateRequest(repository)
            }
        }

        item {
            LoadStateFooter(
                loadState = handleLoadState(repositories.loadState),
                onRetryClick = { }
            )
        }
    }
}

@Composable
fun MainContentItem(
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
        MainContentItemUser(
            uri = repository.owner.avatarUrl,
            userName = repository.owner.login,
            modifier = modifier
        )

        MainContentItemRepoName(
            repoName = repository.name,
            modifier = modifier
        )

        MainContentItemStar(
            repo = repository,
            onClickStar = onClickStar,
            onClickUnStar = onClickUnStar,
            modifier = modifier
        )

        MainContentItemDefaultBranch(
            defaultBranch = repository.defaultBranch,
            modifier = modifier
        )

        MainContentItemUpdatedAt(
            updatedAt = repository.updatedAt,
            modifier = modifier
        )

        HorizontalDivider()
    }
}

@Composable
fun MainContentItemUser(
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
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.padding_small)),
            text = userName
        )
    }
}

@Composable
fun MainContentItemRepoName(
    repoName: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = repoName
    )
}

@Composable
fun MainContentItemStar(
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
                .semantics { drawableID = if (repo.isStarred == true) R.drawable.img_star else R.drawable.img_unstar },
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
fun MainContentItemDefaultBranch(
    defaultBranch: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = defaultBranch
    )
}

@Composable
fun MainContentItemUpdatedAt(
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