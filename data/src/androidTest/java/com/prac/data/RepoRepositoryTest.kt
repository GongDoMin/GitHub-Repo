package com.prac.data

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
import com.prac.data.repository.RepoRepository
import com.prac.data.repository.impl.RepoRepositoryImpl
import com.prac.local.UserLocalDataSource
import com.prac.local.fake.source.FakeUserLocalDataSource
import com.prac.local.room.dao.RemoteKeyDao
import com.prac.local.room.dao.RepositoryDao
import com.prac.local.room.database.RepositoryDatabase
import com.prac.local.room.entity.Owner
import com.prac.local.room.entity.Repository
import com.prac.network.dto.OwnerDto
import com.prac.network.dto.RepoDto
import com.prac.network.fake.FakeRepoApiDataSource
import com.prac.network.fake.FakeRepoStarApiDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
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
    private lateinit var userLocalDataSource: UserLocalDataSource

    private lateinit var repositoryDatabase: RepositoryDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao
    private lateinit var repositoryDao: RepositoryDao

    private lateinit var repoRepository: RepoRepository

    private val pageLoadSize = 10

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        repoApiDataSource = FakeRepoApiDataSource()
        repoStarApiDataSource = FakeRepoStarApiDataSource()
        userLocalDataSource = FakeUserLocalDataSource()

        repositoryDatabase = Room
            .inMemoryDatabaseBuilder(context, RepositoryDatabase::class.java)
            .build()
        remoteKeyDao = repositoryDatabase.remoteKeyDao()
        repositoryDao = repositoryDatabase.repositoryDao()

        repoRepository = RepoRepositoryImpl(repoApiDataSource, repoStarApiDataSource, repositoryDatabase, userLocalDataSource)
    }

    @After
    fun tearDown() = runTest {
        repositoryDatabase.repositoryDao().clearRepositories()
        repositoryDatabase.remoteKeyDao().clearRemoteKeys()
        repositoryDatabase.close()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_loadTypeIsRefresh_successAndEndOfPaginationReachedIsFalse() = runTest {
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
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun load_loadTypeIsRefresh_successAndEndOfPaginationReachedIsTrue() = runTest {
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
    fun load_loadThreeTimes_successResult() = runTest {
        val totalRepoDtoList: MutableList<RepoDto> = mutableListOf()
        val totalPage = 3

        repeat(totalPage) { page ->
            val loadSize = 10
            val pagingState = PagingState(
                pages = listOf(PagingSource.LoadResult.Page(
                    data = totalRepoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, null) },
                    prevKey = 0, // data 의 id 를 통해 remoteKey 를 가져오기 때문에 0 으로 구현
                    nextKey = 0 // data 의 id 를 통해 remoteKey 를 가져오기 때문에 0 으로 구현
                    )
                ),
                anchorPosition = null,
                config = PagingConfig(pageSize = pageLoadSize, enablePlaceholders = false),
                leadingPlaceholderCount = 0
            )
            // (null, 2), (1, 3), (2, 4) .....
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
    fun load_loadIsFailure_errorResult() = runTest {
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
    fun isStarred_userIsStarredRepository_updateRoomStarStateToTrue() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        val repoDto = repoDtoList[index]
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, null) })

        repoRepository.isStarred(repoDto.id, repoDto.name)

        val roomRepository = repositoryDao.getRepository(repoDto.id).first()
        assertEquals(roomRepository?.isStarred, true)
    }

    @Test
    fun isStarred_userIsNotStarredRepository_updateRoomStarStateToFalse() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        val repoDto = repoDtoList[index]
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, null) })
        repoStarApiDataSource.setThrowable(Exception()) // 사용자가 repository 를 star 하고 있지 않을 경우 응답이 304 이기 때문에 예외를 발생시켜서 테스트 진행

        repoRepository.isStarred(repoDto.id, repoDto.name)

        val roomRepository = repositoryDao.getRepository(repoDto.id).first()
        assertEquals(roomRepository?.isStarred, false)
    }

    @Test
    fun starRepository_starRepositoryIsSuccess_successResult() = runTest {
        val userName = "test"
        val repoName = "test"

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isSuccess)
    }

    @Test
    fun starRepository_starRepositoryIsFailure_networkError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(IOException())

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun starRepository_starRepositoryIsFailure_authorizationError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(HttpException(Response.error<Any>(401, "empty body".toResponseBody())))

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun starRepository_starRepositoryIsFailure_notFoundRepositoryError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(HttpException(Response.error<Any>(404, "empty body".toResponseBody())))

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RepositoryException.NotFoundRepository)
    }

    @Test
    fun starRepository_starRepositoryIsFailure_unKnownError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(IllegalArgumentException())

        val result = repoRepository.starRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.UnKnownError)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsSuccess_successResult() = runTest {
        val userName = "test"
        val repoName = "test"

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isSuccess)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsFailure_networkError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(IOException())

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsFailure_authorizationError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(HttpException(Response.error<Any>(401, "empty body".toResponseBody())))

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsFailure_notFoundRepositoryError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(HttpException(Response.error<Any>(404, "empty body".toResponseBody())))

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RepositoryException.NotFoundRepository)
    }

    @Test
    fun unStarRepository_unStarRepositoryIsFailure_unKnownError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoStarApiDataSource.setThrowable(IllegalArgumentException())

        val result = repoRepository.unStarRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.UnKnownError)
    }

    @Test
    fun starLocalRepository_validID_updateStarStateAndCount() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        val repoDto = repoDtoList[index]
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, false) })

        repoRepository.starLocalRepository(repoDto.id, repoDto.stargazersCount + 1)

        val roomRepository = repositoryDao.getRepository(repoDto.id).first()
        assertEquals(roomRepository?.isStarred, true)
        assertEquals(roomRepository?.stargazersCount, repoDto.stargazersCount + 1)
    }

    @Test
    fun unStarLocalRepository_validID_updateStarStateAndCount() = runTest {
        val page = 1
        val loadSize = 10
        val index = 0
        val repoDtoList = getRepoDtoListForPage(page, loadSize)
        val repoDto = repoDtoList[index]
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount + 1, it.updatedAt, true) })

        repoRepository.unStarLocalRepository(repoDto.id, repoDto.stargazersCount)

        val roomRepository = repositoryDao.getRepository(repoDto.id).first()
        assertEquals(roomRepository?.isStarred, false)
        assertEquals(roomRepository?.stargazersCount, repoDto.stargazersCount)
    }

    @Test
    fun getRepository_starCountIsNotChanged_successResult() = runTest {
        val repoDtoList = getRepoDtoListForPage(1, 10)
        val index = 0
        val repoDto = repoDtoList[index]
        repoApiDataSource.setRepoDtoList(repoDtoList)
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, false) })

        val result = repoRepository.getRepository(repoDto.owner.login, repoDto.name)

        val repository = repositoryDatabase.repositoryDao().getRepository(repoDto.id).first()
        assertEquals(repository?.stargazersCount, 0)
        assertTrue(result.isSuccess)
    }

    @Test
    fun getRepository_starCountIsChanged_successResult() = runTest {
        val repoDtoList = getRepoDtoListForPage(1, 10)
        val index = 0
        val repoDto = repoDtoList[index]
        val starCount = 10
        repositoryDatabase.repositoryDao().insertRepositories(repoDtoList.map { Repository(it.id, it.name, Owner(it.owner.login, it.owner.avatarUrl), it.stargazersCount, it.updatedAt, false) })
        repoApiDataSource.setRepoDtoList(repoDtoList)
        repoApiDataSource.setStarCount(starCount)

        val result = repoRepository.getRepository(repoDto.owner.login, repoDto.name)

        val repository = repositoryDatabase.repositoryDao().getRepository(repoDto.id).first()
        assertEquals(repository?.stargazersCount, starCount)
        assertTrue(result.isSuccess)
    }

    @Test
    fun getRepository_getRepositoryIsFailure_networkError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoApiDataSource.setThrowable(IOException())

        val result = repoRepository.getRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun getRepository_getRepositoryIsFailure_authorizationError() = runTest {
        val userName = "test"
        val repoName = "test"
        repoApiDataSource.setThrowable(HttpException(Response.error<Any>(401, "empty body".toResponseBody())))

        val result = repoRepository.getRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun getRepository_getRepositoryIsFailure_notFoundRepository() = runTest {
        val userName = "test"
        val repoName = "test"
        repoApiDataSource.setThrowable(HttpException(Response.error<Any>(404, "empty body".toResponseBody())))

        val result = repoRepository.getRepository(userName, repoName)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RepositoryException.NotFoundRepository)
    }

    @Test
    fun getRepository_getRepositoryIsFailure_unKnownError() = runTest {
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