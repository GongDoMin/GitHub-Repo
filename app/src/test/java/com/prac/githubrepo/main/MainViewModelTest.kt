package com.prac.githubrepo.main

import androidx.paging.PagingData
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoEntity
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.constants.INVALID_REPOSITORY
import com.prac.githubrepo.constants.INVALID_TOKEN
import com.prac.githubrepo.constants.UNKNOWN
import com.prac.githubrepo.util.FakeBackOffWorkManager
import com.prac.githubrepo.util.StandardTestDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    @Mock private lateinit var tokenRepository: TokenRepository
    @Mock private lateinit var repoRepository: RepoRepository
    private lateinit var backOffWork: FakeBackOffWorkManager

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() = runTest {
        backOffWork = FakeBackOffWorkManager()

        val pagingData = PagingData.from(emptyList<RepoEntity>())
        whenever(repoRepository.getRepositories()).thenReturn(flow { emit(pagingData) } )
        mainViewModel = MainViewModel(repoRepository, tokenRepository, backOffWork, standardTestDispatcherRule.testDispatcher)
    }

    @After
    fun tearDown() {
        backOffWork.clearWork()
        backOffWork.clearDelayTimes()
    }

    @Test
    fun getRepositories_updateUiStateToContent() = runTest {
        advanceUntilIdle()

        val result = mainViewModel.uiState.value

        Assert.assertTrue(result is MainViewModel.UiState.Content)
    }

    @Test
    fun starRepository_success_callStarLocalAndRemoteRepository() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(repoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.success(Unit))

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        verify(repoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount + 1)
        verify(repoRepository).starRepository(repoEntity.owner.login, repoEntity.name)
    }

    @Test
    fun unStarRepository_success_callStarLocalAndRemoteRepository() = runTest {
        val repoEntity = makeRepoEntity()

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        verify(repoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount - 1)
        verify(repoRepository).unStarRepository(repoEntity.owner.login, repoEntity.name)
    }

    @Test
    fun starRepository_networkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repoEntity = makeRepoEntity()
        val uniqueID = "star_${repoEntity.id}"
        val callApiTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val delayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(repoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        verify(repoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount + 1)
        verify(repoRepository, times(callApiTimes)).starRepository(repoEntity.owner.login, repoEntity.name)
        Assert.assertEquals(backOffWork.getDelayTimes(uniqueID), delayTimes)
    }

    @Test
    fun unStarRepository_networkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repoEntity = makeRepoEntity()
        val uniqueID = "star_${repoEntity.id}"
        val callApiTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val delayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(repoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        verify(repoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount - 1)
        verify(repoRepository, times(callApiTimes)).unStarRepository(repoEntity.owner.login, repoEntity.name)
        Assert.assertEquals(backOffWork.getDelayTimes(uniqueID), delayTimes)
    }

    @Test
    fun starRepository_authorizationError_updateUiStateDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(repoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        Assert.assertTrue(uiState is MainViewModel.UiState.Content)
        Assert.assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, INVALID_TOKEN)
        verify(tokenRepository).clearToken()
    }

    @Test
    fun unStarRepository_authorizationError_updateUiStateDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(repoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        Assert.assertTrue(uiState is MainViewModel.UiState.Content)
        Assert.assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, INVALID_TOKEN)
        verify(tokenRepository).clearToken()
    }

    @Test
    fun starRepository_repositoryIsNotFoundError_updateUiStateDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(repoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        Assert.assertTrue(uiState is MainViewModel.UiState.Content)
        Assert.assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, INVALID_REPOSITORY)
        verify(repoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)
    }

    @Test
    fun unStarRepository_repositoryIsNotFoundError_updateUiStateDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(repoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        Assert.assertTrue(uiState is MainViewModel.UiState.Content)
        Assert.assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, INVALID_REPOSITORY)
        verify(repoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun starRepository_unKnownError_updateUiStateDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(repoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        Assert.assertTrue(uiState is MainViewModel.UiState.Content)
        Assert.assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, UNKNOWN)
        verify(repoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun unStarRepository_unKnownError_updateUiStateDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(repoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        Assert.assertTrue(uiState is MainViewModel.UiState.Content)
        Assert.assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, UNKNOWN)
        verify(repoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)
    }

    private fun makeRepoEntity() =
        RepoEntity(
            id = 1,
            name = "name",
            owner = OwnerEntity(login = "login", avatarUrl = "avatarUrl"),
            stargazersCount = 10,
            updatedAt = "updatedAt",
            isStarred = null
        )

}