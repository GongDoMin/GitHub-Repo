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