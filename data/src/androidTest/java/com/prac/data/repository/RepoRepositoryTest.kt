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
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.fake.source.network.FakeRepoApiDataSource
import com.prac.data.fake.source.network.FakeRepoStarApiDataSource
import com.prac.data.repository.impl.RepoRepositoryImpl
import com.prac.data.source.local.room.dao.RemoteKeyDao
import com.prac.data.source.local.room.dao.RepositoryDao
import com.prac.data.source.local.room.database.RepositoryDatabase
import com.prac.data.source.local.room.entity.Owner
import com.prac.data.source.local.room.entity.Repository
import com.prac.data.source.network.dto.OwnerDto
import com.prac.data.source.network.dto.RepoDto
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@RunWith(AndroidJUnit4::class)
internal class RepoRepositoryTest {

    private lateinit var repoApiDataSource: FakeRepoApiDataSource
    private lateinit var repoStarApiDataSource: FakeRepoStarApiDataSource

    private lateinit var repositoryDatabase: RepositoryDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao
    private lateinit var repositoryDao: RepositoryDao

    private lateinit var repoRepository: RepoRepository

    private val pageLoadSize = 10
    private val totalPage = 3

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        repoApiDataSource = FakeRepoApiDataSource()
        repoStarApiDataSource = FakeRepoStarApiDataSource()

        repositoryDatabase = Room
            .inMemoryDatabaseBuilder(context, RepositoryDatabase::class.java)
            .allowMainThreadQueries()
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
        repoApiDataSource.setRepoDtoList(repoDtoList)

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
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        val prevKey = null // page 가 1 이기 때문에 prevKey 는 null
        val nextKey = null // loadSize 가 5 이기 때문에 nextKey 는 null
        repoApiDataSource.setRepoDtoList(repoDtoList)

        val result = repoRepository.load(LoadType.REFRESH, pagingState)

        val roomRepositories = (repositoryDao.getRepositories().load(
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
            val repoDtoList = getRepoDtoListForPage(page, loadSize)
            totalRepoDtoList.addAll(repoDtoList)
            repoApiDataSource.setRepoDtoList(repoDtoList)
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
                assertEquals(roomRepositories.data[it].id, repoDtoList[it].id)
                val remoteKey = remoteKeyDao.remoteKey(roomRepositories.data[it].id)
                assertEquals(remoteKey?.repoId, repoDtoList[it].id)
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
        repoApiDataSource.setThrowable(Exception())

        val result = repoRepository.load(LoadType.REFRESH, pagingState)

        val roomRepositoryList = (repositoryDao.getRepositories().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageLoadSize,
                placeholdersEnabled = false
            )
        ) as? PagingSource.LoadResult.Page)?.data
        assertEquals(roomRepositoryList?.size, 0)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun isStarred_userIsStarredRepository() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, null) })

        repoRepository.isStarred(repoDtoList[index].id, repoDtoList[index].name)

        val roomRepository = repositoryDao.getRepository(repoDtoList[index].id).first()
        assertEquals(roomRepository?.isStarred, true)
    }

    @Test
    fun isStarred_userIsNotStarredRepository() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, null) })
        repoStarApiDataSource.setThrowable(Exception())

        repoRepository.isStarred(repoDtoList[index].id, repoDtoList[index].name)

        val roomRepository = repositoryDao.getRepository(repoDtoList[index].id).first()
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
    fun starRepository_returnNetworkError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(IOException())

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun starRepository_returnAuthorizationError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(HttpException(Response.error<Any>(401, "empty body".toResponseBody())))

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun starRepository_returnNotFoundRepositoryError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(HttpException(Response.error<Any>(404, "empty body".toResponseBody())))

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RepositoryException.NotFoundRepository)
    }

    @Test
    fun starRepository_returnUnKnownError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(IllegalArgumentException())

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.UnKnownError)
    }

    @Test
    fun unStarRepository_returnSuccessResult() = runTest {
        val userName = "test"
        val repoName = "test"

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isSuccess)
    }

    @Test
    fun unStarRepository_returnNetworkError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(IOException())

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun unStarRepository_returnAuthorizationError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(HttpException(Response.error<Any>(401, "empty body".toResponseBody())))

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun unStarRepository_returnNotFoundRepositoryError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(HttpException(Response.error<Any>(404, "empty body".toResponseBody())))

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RepositoryException.NotFoundRepository)
    }

    @Test
    fun unStarRepository_returnUnKnownError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(IllegalArgumentException())

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.UnKnownError)
    }

    @Test
    fun starLocalRepository_updatesStarStateAndCount() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, false) })

        repoRepository.starLocalRepository(repoDtoList[index].id, repoDtoList[index].stargazersCount + 1)

        val roomRepository = repositoryDao.getRepository(repoDtoList[index].id).first()
        assertEquals(roomRepository?.isStarred, true)
        assertEquals(roomRepository?.stargazersCount, repoDtoList[index].stargazersCount + 1)
    }

    @Test
    fun unStarLocalRepository_updatesStarStateAndCount() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount + 1, it.updatedAt, true) })

        repoRepository.unStarLocalRepository(repoDtoList[index].id, repoDtoList[index].stargazersCount)

        val roomRepository = repositoryDao.getRepository(repoDtoList[index].id).first()
        assertEquals(roomRepository?.isStarred, false)
        assertEquals(roomRepository?.stargazersCount, repoDtoList[index].stargazersCount)
    }

    @Test
    fun getRepository_starCountIsNotChanged_returnSuccessResult() = runTest {
        val repoDtoList = getRepoDtoListForPage(1, 10)
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, false) })
        repoApiDataSource.setRepoDtoList(repoDtoList)

        val result = repoRepository.getRepository(repoDtoList[0].owner.login, repoDtoList[0].name)

        val repository = repositoryDatabase.repositoryDao().getRepository(repoDtoList[0].id).first()
        assertEquals(repository?.stargazersCount, 0)
        assertTrue(result.isSuccess)
    }

    @Test
    fun getRepository_starCountIsChanged_returnSuccessResult() = runTest {
        val repoDtoList = getRepoDtoListForPage(1, 10)
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, false) })
        repoApiDataSource.setRepoDtoList(repoDtoList)
        repoApiDataSource.setStarCount(10)

        val result = repoRepository.getRepository(repoDtoList[0].owner.login, repoDtoList[0].name)

        val repository = repositoryDatabase.repositoryDao().getRepository(repoDtoList[0].id).first()
        assertEquals(repository?.stargazersCount, 10)
        assertTrue(result.isSuccess)
    }

    @Test
    fun getRepository_returnNetworkError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoApiDataSource.setThrowable(IOException())

        val result = repoRepository.getRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun getRepository_returnAuthorizationError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoApiDataSource.setThrowable(HttpException(Response.error<Any>(401, "empty body".toResponseBody())))

        val result = repoRepository.getRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun getRepository_returnNotFoundRepository() = runTest {
        val userName = "test"
        val repoName = "test"
        repoApiDataSource.setThrowable(HttpException(Response.error<Any>(404, "empty body".toResponseBody())))

        val result = repoRepository.getRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RepositoryException.NotFoundRepository)
    }

    @Test
    fun getRepository_returnUnKnownError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoApiDataSource.setThrowable(IllegalArgumentException())

        val result = repoRepository.getRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.UnKnownError)
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