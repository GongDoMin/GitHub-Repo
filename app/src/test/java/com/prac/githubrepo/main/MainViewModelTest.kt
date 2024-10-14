package com.prac.githubrepo.main

import androidx.paging.PagingData
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoEntity
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.TokenRepository
import com.prac.githubrepo.util.FakeBackOffWorkManager
import com.prac.githubrepo.util.StandardTestDispatcherRule
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

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