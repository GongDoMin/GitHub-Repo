package com.prac.data.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.map
import com.prac.data.model.RepositoryDetail
import com.prac.data.model.Repository
import com.prac.data.repository.RepoRepository
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.model.toRepoDetailModel
import com.prac.data.model.toRepoModel
import com.prac.data.model.toRepository
import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.RepositoryLocalDataSource
import com.prac.local.model.RemoteKeyEntity
import com.prac.local.model.RepositoryEntity
import com.prac.network.RepoApiDataSource
import com.prac.network.RepoStarApiDataSource
import com.prac.network.model.response.RepositoryDetailResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
internal class RepoRepositoryImpl @Inject constructor(
    private val repoApiDataSource: RepoApiDataSource,
    private val repoStarApiDataSource: RepoStarApiDataSource,
    private val repositoryLocalDataSource: RepositoryLocalDataSource,
    private val remoteKeyLocalDataSource: RemoteKeyLocalDataSource,
) : RepoRepository() {

    private var userName: String = ""

    override suspend fun getRepositories(userName: String): Flow<PagingData<Repository>> {
        this.userName = userName

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true
            ),
            remoteMediator = this,
            pagingSourceFactory = { repositoryLocalDataSource.getRepositories() }
        ).flow
            .map { pagingData ->
                pagingData.map { repository ->
                    repository.toRepoModel()
                }
            }
    }

    override suspend fun getRepository(userName: String, repoName: String): Result<RepositoryDetail> = coroutineScope {
        try {
            val issueCount: Int
            val pullCount: Int
            val repositoryDetailResponse: RepositoryDetailResponse
            val readme: String

            val deferredIssueCount = async { repoApiDataSource.getRepoIssueCount(userName, repoName) }
            val deferredPullCount = async { repoApiDataSource.getRepoPullCount(userName, repoName) }
            val deferredRepoDetailDto = async { repoApiDataSource.getRepository(userName, repoName) }
            val deferredReadme = async { repoApiDataSource.getRepoReadme(userName, repoName) }

            issueCount = deferredIssueCount.await()
            pullCount = deferredPullCount.await()
            repositoryDetailResponse = deferredRepoDetailDto.await()
            readme = deferredReadme.await()

            repositoryLocalDataSource.updateStarCount(repositoryDetailResponse.id, repositoryDetailResponse.stargazersCount)

            Result.success(
                repositoryDetailResponse.toRepoDetailModel(
                    issueCount = issueCount,
                    pullCount = pullCount,
                    readme = readme
                )
            )
        } catch (e: Exception) {
            handleRepositoryError(e)
        }
    }

    override suspend fun clearRepositories() {
        repositoryLocalDataSource.clearRepositories()
        remoteKeyLocalDataSource.clearRemoteKeys()
    }

    override suspend fun getStarStateAndStarCount(id: Int): Flow<Pair<Boolean?, Int?>> {
        return repositoryLocalDataSource.getRepository(id).map { Pair(it?.isStarred, it?.stargazersCount) }
    }

    override suspend fun isStarred(id: Int, repoName: String) {
        try {
            repoStarApiDataSource.isStarred(userName, repoName)

            repositoryLocalDataSource.updateStarState(id, true)
        } catch (e: Exception) {
            repositoryLocalDataSource.updateStarState(id, false)
        }
    }

    override suspend fun starRepository(userName: String, repoName: String): Result<Unit> {
        return try {
            repoStarApiDataSource.starRepository(userName, repoName)

            Result.success(Unit)
        } catch (e: Exception) {
            handleRepositoryError(e)
        }
    }

    override suspend fun unStarRepository(userName: String, repoName: String): Result<Unit> {
        return try {
            repoStarApiDataSource.unStarRepository(userName, repoName)

            Result.success(Unit)
        } catch (e: Exception) {
            handleRepositoryError(e)
        }
    }

    override suspend fun starLocalRepository(id: Int, updatedStarCount: Int) {
        repositoryLocalDataSource.updateStarStateAndStarCount(id, true, updatedStarCount)
    }

    override suspend fun unStarLocalRepository(id: Int, updatedStarCount: Int) {
        repositoryLocalDataSource.updateStarStateAndStarCount(id, false, updatedStarCount)
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, RepositoryEntity>): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val response = repoApiDataSource.getRepositories(userName, PAGE_SIZE, page)

            if (loadType == LoadType.REFRESH) {
                remoteKeyLocalDataSource.clearRemoteKeys()
                repositoryLocalDataSource.clearRepositories()
            }
            val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (response.size < 10) null else page + 1
            val keys = response.map {
                RemoteKeyEntity(it.id, prevKey, nextKey)
            }
            val repositories = response.map {
                it.toRepoModel().toRepository()
            }
            remoteKeyLocalDataSource.insertRemoteKeys(keys)
            repositoryLocalDataSource.insertRepositories(repositories)

            return MediatorResult.Success(endOfPaginationReached = response.size < 10)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, RepositoryEntity>): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                remoteKeyLocalDataSource.remoteKey(repoId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, RepositoryEntity>): RemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                remoteKeyLocalDataSource.remoteKey(repo.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, RepositoryEntity>): RemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                remoteKeyLocalDataSource.remoteKey(repo.id)
            }
    }

    private fun <T> handleRepositoryError(e: Exception) : Result<T> {
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    401 -> Result.failure(CommonException.AuthorizationError())
                    404 -> Result.failure(RepositoryException.NotFoundRepository())
                    else -> Result.failure(CommonException.UnKnownError())
                }
            }
            is IOException -> Result.failure(CommonException.NetworkError())
            else -> Result.failure(CommonException.UnKnownError())
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val PAGE_SIZE = 10
    }
}

