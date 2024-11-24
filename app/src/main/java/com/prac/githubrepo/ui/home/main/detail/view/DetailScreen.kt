package com.prac.githubrepo.ui.home.main.detail.view

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
import com.prac.githubrepo.components.UserProfile
import com.prac.githubrepo.ui.home.main.detail.DetailViewModel
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