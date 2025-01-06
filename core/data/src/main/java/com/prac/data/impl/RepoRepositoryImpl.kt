package com.prac.data.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.map
import com.prac.data.model.OwnerModel
import com.prac.data.model.RepoDetailModel
import com.prac.data.model.RepoModel
import com.prac.data.repository.RepoRepository
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.RepositoryLocalDataSource
import com.prac.local.UserLocalDataSource
import com.prac.local.room.entity.Owner
import com.prac.local.room.entity.RemoteKey
import com.prac.local.room.entity.Repository
import com.prac.network.RepoApiDataSource
import com.prac.network.RepoStarApiDataSource
import com.prac.network.dto.RepoDetailDto
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@OptIn(ExperimentalPagingApi::class)
internal class RepoRepositoryImpl @Inject constructor(
    private val repoApiDataSource: RepoApiDataSource,
    private val repoStarApiDataSource: RepoStarApiDataSource,
    private val repositoryLocalDataSource: RepositoryLocalDataSource,
    private val remoteKeyLocalDataSource: RemoteKeyLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : RepoRepository() {

    private var userName: String = ""

    override suspend fun getRepositoriesV2(userName: String): Flow<PagingData<RepoModel>> {
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
                    RepoModel(repository.id, repository.name, OwnerModel(repository.owner.login, repository.owner.avatarUrl), repository.stargazersCount, repository.defaultBranch, repository.updatedAt, repository.isStarred)
                }
            }
    }

    override suspend fun getRepositories(): Flow<PagingData<RepoModel>> {
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
                    RepoModel(repository.id, repository.name, OwnerModel(repository.owner.login, repository.owner.avatarUrl), repository.stargazersCount, repository.defaultBranch, repository.updatedAt, repository.isStarred)
                }
            }
    }

    override suspend fun getRepository(userName: String, repoName: String): Result<RepoDetailModel> {
        return try {
            withContext(EmptyCoroutineContext) {
                val issueCount: Int
                val pullCount: Int
                val repoDetailDto: RepoDetailDto
                val readme: String

                val deferredIssueCount = async { repoApiDataSource.getRepoIssueCount(userName, repoName) }
                val deferredPullCount = async { repoApiDataSource.getRepoPullCount(userName, repoName) }
                val deferredRepoDetailDto = async { repoApiDataSource.getRepository(userName, repoName) }
                val deferredReadme = async { repoApiDataSource.getRepoReadme(userName, repoName) }

                issueCount = deferredIssueCount.await()
                pullCount = deferredPullCount.await()
                repoDetailDto = deferredRepoDetailDto.await()
                readme = deferredReadme.await()

                repositoryLocalDataSource.updateStarCount(repoDetailDto.id, repoDetailDto.stargazersCount)

                Result.success(
                    RepoDetailModel(
                        repoDetailDto.id, repoDetailDto.name, OwnerModel(repoDetailDto.owner.login, repoDetailDto.owner.avatarUrl), repoDetailDto.stargazersCount, repoDetailDto.forksCount, null, issueCount, pullCount, repoDetailDto.subscribersCount, readme
                    )
                )
            }
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

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Repository>): MediatorResult {
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
            val userName = userLocalDataSource.getUserName()
            val response = repoApiDataSource.getRepositories(userName, PAGE_SIZE, page)

            if (loadType == LoadType.REFRESH) {
                remoteKeyLocalDataSource.clearRemoteKeys()
                repositoryLocalDataSource.clearRepositories()
            }
            val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (response.size < 10) null else page + 1
            val keys = response.map {
                RemoteKey(it.id, prevKey, nextKey)
            }
            val repositories = response.map {
                Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, it.defaultBranch, null)
            }
            remoteKeyLocalDataSource.insertRemoteKeys(keys)
            repositoryLocalDataSource.insertRepositories(repositories)

            return MediatorResult.Success(endOfPaginationReached = response.size < 10)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Repository>): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                remoteKeyLocalDataSource.remoteKey(repoId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Repository>): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                remoteKeyLocalDataSource.remoteKey(repo.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Repository>): RemoteKey? {
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

