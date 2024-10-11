package com.prac.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prac.data.repository.impl.RepoRepositoryImpl
import com.prac.data.repository.model.OwnerModel
import com.prac.data.repository.model.RepoDetailModel
import com.prac.data.repository.model.RepoModel
import com.prac.data.source.local.room.dao.RemoteKeyDao
import com.prac.data.source.local.room.dao.RepositoryDao
import com.prac.data.source.local.room.database.RepositoryDatabase
import com.prac.data.source.local.room.entity.Owner
import com.prac.data.source.local.room.entity.Repository
import com.prac.data.source.network.RepoApiDataSource
import com.prac.data.source.network.RepoStarApiDataSource
import com.prac.data.source.network.dto.OwnerDto
import com.prac.data.source.network.dto.RepoDto
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class RepoRepositoryTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private lateinit var repoApiDataSource: MockRepoApiDataSource
    private lateinit var repoStarApiDataSource: MockRepoStarApiDataSource

    private lateinit var repositoryDatabase: RepositoryDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao
    private lateinit var repositoryDao: RepositoryDao

    private lateinit var repoRepository: RepoRepository

    private val pageLoadSize = 10
    private val totalPage = 3

    @Before
    fun setUp() {
        repoApiDataSource = MockRepoApiDataSource()
        repoStarApiDataSource = MockRepoStarApiDataSource()

        repositoryDatabase = Room.inMemoryDatabaseBuilder(context, RepositoryDatabase::class.java)
            .build()
        remoteKeyDao = repositoryDatabase.remoteKeyDao()
        repositoryDao = repositoryDatabase.repositoryDao()

        repoRepository = RepoRepositoryImpl(repoApiDataSource, repoStarApiDataSource, repositoryDatabase)
    }

    @After
    fun tearDown() = runTest {
        repositoryDatabase.repositoryDao().clearRepositories()
        repositoryDatabase.remoteKeyDao().clearRemoteKeys()
        repositoryDatabase.close()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_withRefresh_returnSuccessResult_and_endOfPaginationReachedIsFalse() = runTest {
        val loadSize = 10
        val page = 1
        val pagingState = PagingState<Int, Repository>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = pageLoadSize, enablePlaceholders = false),
            leadingPlaceholderCount = 0
        )
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        val prevKey = null // page 가 1 이기 때문에 prevKey 는 null
        val nextKey = page + 1 // loadSize 가 10 이기 때문에 nextKey 는 page + 1
        repoApiDataSource.thenRepoDtoList(repoDtoList)

        val result = repoRepository.load(LoadType.REFRESH, pagingState)

        val roomRepositories = (repositoryDao.getRepositories()
            .load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = pageLoadSize,
                    placeholdersEnabled = false
                )
            ) as? PagingSource.LoadResult.Page)?.data
        assertEquals(roomRepositories?.size, repoDtoList.size)
        repoDtoList.indices.forEach {
            assertEquals(roomRepositories?.get(it)?.id, repoDtoList[it].id)
            val remoteKey = remoteKeyDao.remoteKey(repoDtoList[it].id)
            assertEquals(remoteKey?.repoId, repoDtoList[it].id)
            assertEquals(remoteKey?.prevKey, prevKey)
            assertEquals(remoteKey?.nextKey, nextKey)
        }
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_withRefresh_returnSuccessResult_and_endOfPaginationReachedIsTrue() = runTest {
        val loadSize = 5
        val page = 1
        val pagingState = PagingState<Int, Repository>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = pageLoadSize, enablePlaceholders = false),
            leadingPlaceholderCount = 0
        )
        val repositoriesDto = getRepoDtoListForPage(page, loadSize)
        val prevKey = null // page 가 1 이기 때문에 prevKey 는 null
        val nextKey = null // loadSize 가 5 이기 때문에 nextKey 는 null
        repoApiDataSource.thenRepoDtoList(repositoriesDto)

        val result = repoRepository.load(LoadType.REFRESH, pagingState)

        val roomRepositories = (repositoryDao.getRepositories().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageLoadSize,
                placeholdersEnabled = false
            )
        ) as? PagingSource.LoadResult.Page)?.data
        assertEquals(roomRepositories?.size, repositoriesDto.size)
        repositoriesDto.indices.forEach {
            assertEquals(roomRepositories?.get(it)?.id, repositoriesDto[it].id)
            val remoteKey = remoteKeyDao.remoteKey(repositoriesDto[it].id)
            assertEquals(remoteKey?.repoId, repositoriesDto[it].id)
            assertEquals(remoteKey?.prevKey, prevKey)
            assertEquals(remoteKey?.nextKey, nextKey)
        }
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_multipleTimes_returnSuccessResult() = runTest {
        val totalRepoDtoList: MutableList<RepoDto> = mutableListOf()

        repeat(totalPage) { page ->
            val loadSize = 10
            val pagingState = PagingState(
                pages = listOf(PagingSource.LoadResult.Page(
                    data = totalRepoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, null) },
                    prevKey = 0,
                    nextKey = 0
                    )
                ),
                anchorPosition = null,
                config = PagingConfig(pageSize = pageLoadSize, enablePlaceholders = false),
                leadingPlaceholderCount = 0
            )
            val prevKey = if (page == 0) null else page
            val nextKey = page + 2
            val repositoriesDto = getRepoDtoListForPage(page, loadSize)
            totalRepoDtoList.addAll(repositoriesDto)
            repoApiDataSource.thenRepoDtoList(repositoriesDto)
            val loadParams =
                if (page == 0)
                    PagingSource.LoadParams.Refresh(
                        key = page,
                        loadSize = pageLoadSize,
                        placeholdersEnabled = false
                    )
                else
                    PagingSource.LoadParams.Append(
                        key = pageLoadSize * page,
                        loadSize = pageLoadSize,
                        placeholdersEnabled = false
                    )

            val result = repoRepository.load(
                if (page == 0) LoadType.REFRESH else LoadType.APPEND,
                pagingState
            )

            val roomRepositories = (repositoryDao.getRepositories().load(loadParams) as? PagingSource.LoadResult.Page)
            assertEquals(roomRepositories?.data?.size, pageLoadSize)
            roomRepositories?.data?.indices?.forEach {
                assertEquals(roomRepositories.data[it].id, repositoriesDto[it].id)
                val remoteKey = remoteKeyDao.remoteKey(roomRepositories.data[it].id)
                assertEquals(remoteKey?.repoId, repositoriesDto[it].id)
                assertEquals(remoteKey?.prevKey, prevKey)
                assertEquals(remoteKey?.nextKey, nextKey)
            }
            assertTrue(result is RemoteMediator.MediatorResult.Success)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_returnsFailureResult() = runTest {
        val pagingState = PagingState<Int, Repository>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = pageLoadSize, enablePlaceholders = false),
            leadingPlaceholderCount = 0
        )
        repoApiDataSource.thenThrow(Exception())

        val result = repoRepository.load(LoadType.REFRESH, pagingState)

        val roomRepositories = (repositoryDao.getRepositories().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageLoadSize,
                placeholdersEnabled = false
            )
        ) as? PagingSource.LoadResult.Page)?.data
        assertEquals(roomRepositories?.size, 0)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun isStarred_userIsStarredRepository() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repositoriesDto = getRepoDtoListForPage(page, loadSize)
        repositoryDatabase.repositoryDao().insertRepositories(repositoriesDto.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, null) })

        repoRepository.isStarred(repositoriesDto[index].id, repositoriesDto[index].name)

        val roomRepository = repositoryDao.getRepository(repositoriesDto[index].id).first()
        assertEquals(roomRepository?.isStarred, true)
    }

    @Test
    fun isStarred_userIsNotStarredRepository() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repositoriesDto = getRepoDtoListForPage(page, loadSize)
        repositoryDatabase.repositoryDao().insertRepositories(repositoriesDto.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, null) })
        repoStarApiDataSource.thenThrow(Exception())

        repoRepository.isStarred(repositoriesDto[index].id, repositoriesDto[index].name)

        val roomRepository = repositoryDao.getRepository(repositoriesDto[index].id).first()
        assertEquals(roomRepository?.isStarred, false)
    }

    @Test
    fun starRepository_returnSuccessResult() = runTest {
        val userName = "test"
        val repoName = "test"

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isSuccess)
    }

    @Test
    fun starRepository_returnFailureResult() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.thenThrow(Exception())

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isFailure)
    }

    @Test
    fun unStarRepository_returnSuccessResult() = runTest {
        val userName = "test"
        val repoName = "test"

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isSuccess)
    }

    @Test
    fun unStarRepository_returnFailureResult() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.thenThrow(Exception())

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isFailure)
    }

    @Test
    fun starLocalRepository_updatesStarStateAndCount() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repositoriesDto = getRepoDtoListForPage(page, loadSize)
        repositoryDatabase.repositoryDao().insertRepositories(repositoriesDto.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, false) })

        repoRepository.starLocalRepository(repositoriesDto[index].id, repositoriesDto[index].stargazersCount + 1)

        val roomRepository = repositoryDao.getRepository(repositoriesDto[index].id).first()
        assertEquals(roomRepository?.isStarred, true)
        assertEquals(roomRepository?.stargazersCount, repositoriesDto[index].stargazersCount + 1)
    }

    private class MockRepoApiDataSource : RepoApiDataSource {

        private lateinit var throwable: Throwable
        private lateinit var repoDtoList: List<RepoDto>
        private lateinit var repoDetailModel: RepoDetailModel

        fun thenThrow(throwable: Throwable) {
            this.throwable = throwable
        }

        fun thenRepoDtoList(repoDtoList: List<RepoDto>) {
            this.repoDtoList = repoDtoList
        }

        fun thenRepository(repoDetailModel: RepoDetailModel) {
            this.repoDetailModel = repoDetailModel
        }


        override suspend fun getRepositories(userName: String, perPage: Int, page: Int): List<RepoModel> {
            if (!::throwable.isInitialized && !::repoDtoList.isInitialized) {
                throw Exception("getRepositories is not initialized")
            }

            if (::throwable.isInitialized) {
                throw throwable
            }

            return repoDtoList.map { RepoModel(it.id, it.name, OwnerModel(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt) }
        }

        override suspend fun getRepository(userName: String, repoName: String): RepoDetailModel {
            if (!::throwable.isInitialized && !::repoDetailModel.isInitialized) {
                throw Exception("getRepositories is not initialized")
            }

            if (::throwable.isInitialized) {
                throw throwable
            }

            return repoDetailModel
        }
    }

    private class MockRepoStarApiDataSource: RepoStarApiDataSource {

        private lateinit var throwable: Throwable

        fun thenThrow(throwable: Throwable) {
            this.throwable = throwable
        }

        override suspend fun checkRepositoryIsStarred(repoName: String) {
            if (::throwable.isInitialized) {
                throw throwable
            }
        }

        override suspend fun starRepository(userName: String, repoName: String) {
            if (::throwable.isInitialized) {
                throw throwable
            }
        }

        override suspend fun unStarRepository(userName: String, repoName: String) {
            if (::throwable.isInitialized) {
                throw throwable
            }
        }
    }

    private fun getRepoDtoListForPage(page : Int, loadSize: Int) : List<RepoDto> =
        mutableListOf<RepoDto>().apply {
            repeat(loadSize) {
                add(
                    RepoDto(it + (10 * page), "test ${it + (10 * page)}", OwnerDto("login ${it + (10 * page)}", "avatarUrl ${it + (10 * page)}"), 0, "2022-01-01")
                )
            }
        }
}