package com.prac.githubrepo.main

import androidx.paging.PagingData
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoEntity
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.fake.FakeTokenRepository
import com.prac.data.repository.RepoRepository
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    private lateinit var tokenRepository: FakeTokenRepository
    @Mock private lateinit var mockRepoRepository: RepoRepository
    private lateinit var backOffWork: FakeBackOffWorkManager

    private lateinit var mainViewModel: MainViewModel


    @Before
    fun setUp() = runTest {
        tokenRepository = FakeTokenRepository().apply { setInitialToken() }
        backOffWork = FakeBackOffWorkManager()

        val pagingData = PagingData.from(emptyList<RepoEntity>())
        whenever(mockRepoRepository.getRepositories()).thenReturn(flow { emit(pagingData) } )

        mainViewModel = MainViewModel(mockRepoRepository, tokenRepository, backOffWork, standardTestDispatcherRule.testDispatcher)
    }

    @After
    fun tearDown() {
        backOffWork.clearWork()
        backOffWork.clearDelayTimes()
    }

    @Test
    fun getRepositories_getRepositoriesIsSuccess_updateUiStateToContent() = runTest {
        advanceUntilIdle()

        val result = mainViewModel.uiState.value

        assertTrue(result is MainViewModel.UiState.Content)
    }

    @Test
    fun starRepository_starRepositoryIsSuccess_callStarLocalAndRemoteRepository() = runTest {
        val repoEntity = makeRepoEntity()

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount + 1)
        verify(mockRepoRepository).starRepository(repoEntity.owner.login, repoEntity.name)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsSuccess_callStarLocalAndRemoteRepository() = runTest {
        val repoEntity = makeRepoEntity()

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount - 1)
        verify(mockRepoRepository).unStarRepository(repoEntity.owner.login, repoEntity.name)
    }

    @Test
    fun starRepository_starRepositoryIsNetworkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repoEntity = makeRepoEntity()
        val uniqueID = "star_${repoEntity.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount + 1)
        verify(mockRepoRepository, times(expectedCallTimes)).starRepository(repoEntity.owner.login, repoEntity.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsNetworkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repoEntity = makeRepoEntity()
        val uniqueID = "star_${repoEntity.id}"
        val expectedCallTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val expectedDelayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount - 1)
        verify(mockRepoRepository, times(expectedCallTimes)).unStarRepository(repoEntity.owner.login, repoEntity.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), expectedDelayTimes)
    }

    @Test
    fun starRepository_starRepositoryIsAuthorizationError_uiStateHasDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        assertTrue(uiState is MainViewModel.UiState.Content)
        assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, INVALID_TOKEN)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsAuthorizationError_uiStateHasDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        assertTrue(uiState is MainViewModel.UiState.Content)
        assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, INVALID_TOKEN)
    }

    @Test
    fun starRepository_starRepositoryIsNotFoundRepositoryError_uiStateHasDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        assertTrue(uiState is MainViewModel.UiState.Content)
        assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, INVALID_REPOSITORY)
        verify(mockRepoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsNotFoundRepositoryError_uiStateHasDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        assertTrue(uiState is MainViewModel.UiState.Content)
        assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, INVALID_REPOSITORY)
        verify(mockRepoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun starRepository_starRepositoryIsUnKnownError_uiStateHasDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.starRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        mainViewModel.starRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        assertTrue(uiState is MainViewModel.UiState.Content)
        assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, UNKNOWN)
        verify(mockRepoRepository).unStarLocalRepository(repoEntity.id, repoEntity.stargazersCount)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun unStarRepository_unStarRepositoryIsUnKnownError_uiStateHasDialogMessage() = runTest {
        val repoEntity = makeRepoEntity()
        whenever(mockRepoRepository.unStarRepository(repoEntity.owner.login, repoEntity.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        mainViewModel.unStarRepository(repoEntity)
        advanceUntilIdle()

        val uiState = mainViewModel.uiState.value
        assertTrue(uiState is MainViewModel.UiState.Content)
        assertEquals((uiState as MainViewModel.UiState.Content).dialogMessage, UNKNOWN)
        verify(mockRepoRepository).starLocalRepository(repoEntity.id, repoEntity.stargazersCount)
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