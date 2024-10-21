package com.prac.githubrepo.main.detail

import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoDetailEntity
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.fake.FakeTokenRepository
import com.prac.data.repository.RepoRepository
import com.prac.githubrepo.constants.CONNECTION_FAIL
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
class DetailViewModelTest {

    @get:Rule
    val standardTestDispatcherRule = StandardTestDispatcherRule()

    private lateinit var tokenRepository: FakeTokenRepository
    @Mock private lateinit var mockRepoRepository: RepoRepository
    private lateinit var backOffWork: FakeBackOffWorkManager

    private lateinit var detailViewMock: DetailViewModel

    @Before
    fun setUp() = runTest {
        tokenRepository = FakeTokenRepository()
        tokenRepository.setInitialToken()

        backOffWork = FakeBackOffWorkManager()

        detailViewMock = DetailViewModel(mockRepoRepository, tokenRepository, backOffWork, standardTestDispatcherRule.testDispatcher)
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
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.success(repoDetailEntity))
        whenever(mockRepoRepository.getStarStateAndStarCount(repoDetailEntity.id))
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
        whenever(mockRepoRepository.getRepository(userName, repoName))
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
        whenever(mockRepoRepository.getRepository(userName, repoName))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        detailViewMock.getRepository(userName, repoName)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, INVALID_TOKEN)
        assertTrue(tokenRepository.getAccessToken().isEmpty())
        assertTrue(tokenRepository.getRefreshToken().isEmpty())
    }

    @Test
    fun starRepository_success_callStarLocalAndRemoteRepository() = runTest {
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.success(Unit))

        detailViewMock.starRepository(repoDetailEntity)
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount + 1)
        verify(mockRepoRepository).starRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
    }

    @Test
    fun unStarRepository_success_callStarLocalAndRemoteRepository() = runTest {
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.success(Unit))

        detailViewMock.unStarRepository(repoDetailEntity)
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount - 1)
        verify(mockRepoRepository).unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
    }

    @Test
    fun starRepository_networkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repoDetailEntity = makeRepoDetailEntity()
        val uniqueID = "star_${repoDetailEntity.id}"
        val callApiTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val delayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        detailViewMock.starRepository(repoDetailEntity)
        advanceUntilIdle()

        verify(mockRepoRepository).starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount + 1)
        verify(mockRepoRepository, times(callApiTimes)).starRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), delayTimes)
    }

    @Test
    fun unStarRepository_networkError_callMultipleTimesRemoteRepository() = runTest {
        backOffWork.setScope(this)
        val repoDetailEntity = makeRepoDetailEntity()
        val uniqueID = "star_${repoDetailEntity.id}"
        val callApiTimes = 6 // backOffWorkManager maxTimes(5) + default(1) = 6
        val delayTimes = 31_000L // 1초 -> 2초 -> 4초 -> 8초 -> 16초 = 31초
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(CommonException.NetworkError()))

        detailViewMock.unStarRepository(repoDetailEntity)
        advanceUntilIdle()

        verify(mockRepoRepository).unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount - 1)
        verify(mockRepoRepository, times(callApiTimes)).unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name)
        assertEquals(backOffWork.getDelayTimes(uniqueID), delayTimes)
    }

    @Test
    fun starRepository_authorizationError_updateUiStateToError() = runTest {
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        detailViewMock.starRepository(repoDetailEntity)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, INVALID_TOKEN)
        assertTrue(tokenRepository.getAccessToken().isEmpty())
        assertTrue(tokenRepository.getRefreshToken().isEmpty())
    }

    @Test
    fun unStarRepository_authorizationError_updateUiStateToError() = runTest {
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(CommonException.AuthorizationError()))

        detailViewMock.unStarRepository(repoDetailEntity)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, INVALID_TOKEN)
        assertTrue(tokenRepository.getAccessToken().isEmpty())
        assertTrue(tokenRepository.getRefreshToken().isEmpty())
    }

    @Test
    fun starRepository_repositoryIsNotFoundError_updateUiStateToError() = runTest {
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        detailViewMock.starRepository(repoDetailEntity)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, INVALID_REPOSITORY)
        verify(mockRepoRepository).unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)
    }

    @Test
    fun unStarRepository_repositoryIsNotFoundError_updateUiStateToError() = runTest {
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(RepositoryException.NotFoundRepository()))

        detailViewMock.unStarRepository(repoDetailEntity)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, INVALID_REPOSITORY)
        verify(mockRepoRepository).starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)
    }

    @Test
    fun starRepository_unKnownError_updateUiStateToError() = runTest {
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.starRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        detailViewMock.starRepository(repoDetailEntity)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, UNKNOWN)
        verify(mockRepoRepository).unStarLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)
    }

    @Test
    fun unStarRepository_unKnownError_updateUiStateToError() = runTest {
        val repoDetailEntity = makeRepoDetailEntity()
        whenever(mockRepoRepository.unStarRepository(repoDetailEntity.owner.login, repoDetailEntity.name))
            .thenReturn(Result.failure(CommonException.UnKnownError()))

        detailViewMock.unStarRepository(repoDetailEntity)
        advanceUntilIdle()

        val uiState = detailViewMock.uiState.value
        assertTrue(uiState is DetailViewModel.UiState.Error)
        assertEquals((uiState as DetailViewModel.UiState.Error).errorMessage, UNKNOWN)
        verify(mockRepoRepository).starLocalRepository(repoDetailEntity.id, repoDetailEntity.stargazersCount)
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