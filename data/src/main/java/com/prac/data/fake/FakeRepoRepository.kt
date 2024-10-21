package com.prac.data.fake

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.map
import androidx.room.withTransaction
import com.prac.data.entity.OwnerEntity
import com.prac.data.entity.RepoDetailEntity
import com.prac.data.entity.RepoEntity
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.model.OwnerModel
import com.prac.data.repository.model.RepoDetailModel
import com.prac.local.room.database.RepositoryDatabase
import com.prac.local.room.entity.Owner
import com.prac.local.room.entity.RemoteKey
import com.prac.local.room.entity.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FakeRepoRepository @Inject constructor(
    private val repositoryDatabase: RepositoryDatabase
): RepoRepository() {
    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getRepositories(): Flow<PagingData<RepoEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            remoteMediator = this,
            pagingSourceFactory = { repositoryDatabase.repositoryDao().getRepositories() }
        ).flow
            .map { pagingData ->
                pagingData.map { repository ->
                    RepoEntity(repository.id, repository.name, OwnerEntity(repository.owner.login, repository.owner.avatarUrl), repository.stargazersCount, repository.updatedAt, repository.isStarred)
                }
            }
    }

    override suspend fun getRepository(userName: String, repoName: String): Result<RepoDetailEntity> {
        val id = userName.split(" ")[1] // userName 은 "login id" 형식으로 되어있다.
        val model = RepoDetailModel(id.toInt(), "test $id", OwnerModel("login $id", "avatarUrl $id"), 5, 5)

        return Result.success(
            RepoDetailEntity(
                model.id, model.name, OwnerEntity(model.owner.login, model.owner.avatarUrl), model.stargazersCount, model.forksCount, null
            )
        )
    }

    override suspend fun getStarStateAndStarCount(id: Int): Flow<Pair<Boolean?, Int?>> {
        return repositoryDatabase.repositoryDao().getRepository(id).map { Pair(it?.isStarred, it?.stargazersCount) }
    }

    override suspend fun isStarred(id: Int, repoName: String) {
        if (id % 2 == 0) repositoryDatabase.repositoryDao().updateStarState(id, true)
        else repositoryDatabase.repositoryDao().updateStarState(id, false)
    }

    override suspend fun starRepository(userName: String, repoName: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun unStarRepository(userName: String, repoName: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun starLocalRepository(id: Int, updatedStarCount: Int) {
        repositoryDatabase.repositoryDao().updateStarStateAndStarCount(id, true, updatedStarCount)
    }

    override suspend fun unStarLocalRepository(id: Int, updatedStarCount: Int) {
        repositoryDatabase.repositoryDao().updateStarStateAndStarCount(id, false, updatedStarCount)
    }

    @ExperimentalPagingApi
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
            val response = makeRepoEntityList(page - 1)

            repositoryDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    repositoryDatabase.remoteKeyDao().clearRemoteKeys()
                    repositoryDatabase.repositoryDao().clearRepositories()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (response.size < PAGE_SIZE) null else page + 1
                val keys = response.map {
                    RemoteKey(it.id, prevKey, nextKey)
                }
                val repositories = response.map {
                    Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, null)
                }
                repositoryDatabase.remoteKeyDao().insertRemoteKeys(keys)
                repositoryDatabase.repositoryDao().insertRepositories(repositories)
            }
            return MediatorResult.Success(endOfPaginationReached = response.size < 10)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Repository>): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                repositoryDatabase.remoteKeyDao().remoteKey(repoId)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Repository>): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                repositoryDatabase.remoteKeyDao().remoteKey(repo.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Repository>): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                repositoryDatabase.remoteKeyDao().remoteKey(repo.id)
            }
    }

    private fun makeRepoEntityList(page: Int) : List<RepoEntity> {
        val pagingData: MutableList<RepoEntity> = mutableListOf()

        repeat(10) {
            pagingData.add(
                RepoEntity(id = it + (10 * page), name = "test ${it + (10 * page)}", owner = OwnerEntity("login ${it + (10 * page)}", "avatarUrl ${it + (10 * page)}"), stargazersCount = 5, updatedAt = "update", isStarred = null)
            )
        }
        // listOf(
        //      RepoEntity(id = 0, name = "test 0", owner = OwnerEntity("login 0", "avatarUrl 0"), stargazersCount = 5, updatedAt = "update", isStarred = null),
        //      RepoEntity(id = 1, name = "test 1", owner = OwnerEntity("login 1", "avatarUrl 1"), stargazersCount = 5, updatedAt = "update", isStarred = null),
        //      .....
        // )

        return pagingData
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
        private const val PAGE_SIZE = 10
    }
}