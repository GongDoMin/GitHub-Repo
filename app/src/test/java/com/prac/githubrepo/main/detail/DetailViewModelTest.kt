package com.prac.githubrepo.main.detail

import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoDetailEntity
import com.prac.data.exception.CommonException
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.constants.CONNECTION_FAIL
import com.prac.githubrepo.constants.INVALID_TOKEN
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    @Mock private lateinit var tokenRepository: TokenRepository
    @Mock private lateinit var repoRepository: RepoRepository
    private lateinit var backOffWork: FakeBackOffWorkManager

    private lateinit var detailViewMock: DetailViewModel

    @Before
    fun setUp() = runTest {
        backOffWork = FakeBackOffWorkManager()

        detailViewMock = DetailViewModel(repoRepository, tokenRepository, backOffWork, standardTestDispatcherRule.testDispatcher)
    }

    @After
    fun tearDown() {
        backOffWork.clearWork()
        backOffWork.clearDelayTimes()
    }

    @Test
    fun getRepository_validInput_updatesUiStateToContent() = runTest {
        val userName = "test"
        val repoName = "test"
        val repoDetailEntity = makeRepoDetailEntity()
        val starStateAndCount = Pair(true, 11)
        whenever(repoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(repoRepository.getStarStateAndStarCount(repoDetailEntity.id))
            .thenReturn(flow { emit(starStateAndCount) })

        detailViewMock.getRepository(userName, repoName)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        val expectedValue = makeExpectedValue(starStateAndCount.first, starStateAndCount.second)
        assertTrue(uiState is DetailViewModel.UiState.Content)
        assertEquals((uiState as DetailViewModel.UiState.Content).repository, expectedValue)
    }

    @Test
    fun getRepository_invalidInput_updatesUiStateToError() = runTest {
        val userName = null
        val repoName = null

        detailViewMock.getRepository(userName, repoName)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, "잘못된 접근입니다.")
    }

    @Test
    fun getRepository_networkError_updatesUiStateToError() = runTest {
        val userName = "test"
        val repoName = "test"
        whenever(repoRepository.getRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        detailViewMock.getRepository(userName, repoName)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, CONNECTION_FAIL)
    }

    @Test
    fun getRepository_authorizationError_updatesUiStateToError() = runTest {
        val userName = "test"
        val repoName = "test"
        whenever(repoRepository.getRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        detailViewMock.getRepository(userName, repoName)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, INVALID_TOKEN)
        verify(tokenRepository).clearToken()
    }

    @Test
    fun starRepository_success_callStarLocalAndRemoteRepository() = runTest {
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(repoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.success(Unit))

        detailViewMock.starRepository(repoDetailEntity)
        advanceUntilIdle()

        verify(repoRepository).starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount + 1)
        verify(repoRepository).starRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
    }

    private fun makeRepoDetailEntity() =
        RepoDetailEntity(
            id = 1,
            name = "test",
            owner = OwnerEntity(login = "test", avatarUrl = "test"),
            stargazersCount = 10,
            forksCount = 10,
            isStarred = true
        )

    private fun makeExpectedValue(isStarred: Boolean, stargazersCount: Int) =
        RepoDetailEntity(
            id = 1,
            name = "test",
            owner = OwnerEntity(login = "test", avatarUrl = "test"),
            stargazersCount = stargazersCount,
            forksCount = 10,
            isStarred = isStarred
        )
}