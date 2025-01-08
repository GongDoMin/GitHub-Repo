package com.prac.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.prac.data.exception.CommonException
import com.prac.data.exception.RepositoryException
import com.prac.data.impl.RepoRepositoryImpl
import com.prac.data.model.RepositoryDetail
import com.prac.data.model.toLocalModel
import com.prac.data.model.toModel
import com.prac.local.RemoteKeyLocalDataSource
import com.prac.local.RepositoryLocalDataSource
import com.prac.local.model.RepositoryEntity
import com.prac.network.RepoApiDataSource
import com.prac.network.RepoStarApiDataSource
import com.prac.network.model.response.OwnerResponse
import com.prac.network.model.response.RepositoryResponse
import com.prac.shared_test.local.source.FakeRemoteKeyLocalDataSource
import com.prac.shared_test.local.source.FakeRepositoryLocalDataSource
import com.prac.shared_test.network.FakeRepoApiDataSource
import com.prac.shared_test.network.FakeRepoStarApiDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
internal class RepoRepositoryTest {

    private lateinit var repoApiDataSource: RepoApiDataSource
    private lateinit var repoStarApiDataSource: RepoStarApiDataSource
    private lateinit var repositoryLocalDataSource: RepositoryLocalDataSource
    private lateinit var remoteKeyLocalDataSource: RemoteKeyLocalDataSource

    private lateinit var repoRepository: RepoRepository

    private val pageLoadSize = 10

    @Test
    fun 성공적으로_데이터로드_그리고_페이지네이션_false() = runTest {
        // given
        val pagingState = PagingState<Int, RepositoryEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = pageLoadSize, enablePlaceholders = false),
            leadingPlaceholderCount = 0
        )
        val page = 1
        val loadSize = 10
        val fakeRepositories = makeFakeRepositories(page, loadSize)
        val expectedPrevKey = null
        val expectedNextKey = page + 1
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(fakeRepositories),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )

        // when
        val result = repoRepository.load(LoadType.REFRESH, pagingState)

        // then
        validateRepositoriesInDatabase(
            fakeRepositories = fakeRepositories,
            expectedPrevKey = expectedPrevKey,
            expectedNextKey = expectedNextKey,
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageLoadSize,
                placeholdersEnabled = false
            )
        )
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun 성공적으로_데이터로드_그리고_페이지네이션_true() = runTest {
        // given
        val pagingState = PagingState<Int, RepositoryEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = pageLoadSize, enablePlaceholders = false),
            leadingPlaceholderCount = 0
        )
        val page = 1
        val loadSize = 5
        val fakeRepositories = makeFakeRepositories(page, loadSize)
        val expectedPrevKey = null
        val expectedNextKey = null
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(fakeRepositories),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )

        // when
        val result = repoRepository.load(LoadType.REFRESH, pagingState)

        // then
        validateRepositoriesInDatabase(
            fakeRepositories = fakeRepositories,
            expectedPrevKey = expectedPrevKey,
            expectedNextKey = expectedNextKey,
            params = PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageLoadSize,
                placeholdersEnabled = false
            )
        )
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun 성공적으로_3번_데이터로드_그리고_페이지네이션_false() = runTest {
        val totalRepositories: MutableList<RepositoryResponse> = mutableListOf()
        val callTimes = 3
        repositoryLocalDataSource = FakeRepositoryLocalDataSource()
        remoteKeyLocalDataSource = FakeRemoteKeyLocalDataSource()

        repeat(callTimes) { page ->
            // given
            val loadSize = 10
            val pagingState = PagingState(
                pages = listOf(
                    // data 의 id 를 통해 remoteKey 를 가져오기 때문에 prevKey, nextKey 를 0 으로 구현
                    PagingSource
                        .LoadResult
                        .Page(
                            data = totalRepositories.map { it.toModel().toLocalModel() },
                            prevKey = 0,
                            nextKey = 0
                        )
                    ),
                anchorPosition = null,
                config = PagingConfig(pageSize = pageLoadSize, enablePlaceholders = false),
                leadingPlaceholderCount = 0
            )
            // (null, 2), (1, 3), (2, 4)
            val expectedPrevKey = if (page == 0) null else page
            val expectedNextKey = page + 2
            val fakeRepositories = makeFakeRepositories(page, loadSize)
            totalRepositories.addAll(fakeRepositories)
            repoApiDataSource = FakeRepoApiDataSource(fakeRepositories)
            repoStarApiDataSource = FakeRepoStarApiDataSource()
            repoRepository = RepoRepositoryImpl(repoApiDataSource, repoStarApiDataSource, repositoryLocalDataSource, remoteKeyLocalDataSource)
            val loadParams =
                if (page == 0)
                    PagingSource.LoadParams.Refresh(
                        key = 1,
                        loadSize = pageLoadSize,
                        placeholdersEnabled = false
                    )
                else
                    PagingSource.LoadParams.Append(
                        key = page + 1,
                        loadSize = pageLoadSize,
                        placeholdersEnabled = false
                    )

            // when
            val result = repoRepository.load(
                if (page == 0) LoadType.REFRESH else LoadType.APPEND,
                pagingState
            )

            // then
            validateRepositoriesInDatabase(
                fakeRepositories = fakeRepositories,
                expectedPrevKey = expectedPrevKey,
                expectedNextKey = expectedNextKey,
                params = loadParams
            )
            assertTrue(result is RemoteMediator.MediatorResult.Success)
            assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }
    }

    @Test
    fun 데이터로드_실패_에러_반환() = runTest {
        // given
        val pagingState = PagingState<Int, RepositoryEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = pageLoadSize, enablePlaceholders = false),
            leadingPlaceholderCount = 0
        )
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(
                repositories = makeFakeRepositories(1, 10),
                throwable = Exception()
            ),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )

        // when
        val result = repoRepository.load(LoadType.REFRESH, pagingState)

        // then
        val repositories = repositoryLocalDataSource.getRepositories().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageLoadSize,
                placeholdersEnabled = false
            )
        )
        val data = (repositories as? PagingSource.LoadResult.Page)?.data
        assertEquals(data?.size, 0)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun Star를_했다면_로컬저장소_별상태_true로_변환() = runTest {
        // given
        val fakeRepositories = makeFakeRepositories(1, 10)
        val index = fakeRepositories.indices.first
        val fakeRepository = fakeRepositories[index]
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )
        repositoryLocalDataSource.insertRepositories(fakeRepositories.map { it.toModel().toLocalModel() })

        // when
        repoRepository.isStarred(fakeRepository.id, fakeRepository.name)

        // then
        val repositories = repositoryLocalDataSource.getRepository(fakeRepository.id).first()
        assertEquals(repositories?.isStarred, true)
    }

    @Test
    fun Star를_하지않았다면_로컬저장소_별상태_false로_변환() = runTest {
        // given
        val fakeRepositories = makeFakeRepositories(1, 10)
        val index = fakeRepositories.indices.first
        val fakeRepository = fakeRepositories[index]
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource(Exception())
        )
        repositoryLocalDataSource.insertRepositories(fakeRepositories.map { it.toModel().toLocalModel() })

        // when
        repoRepository.isStarred(fakeRepository.id, fakeRepository.name)

        // then
        val repositories = repositoryLocalDataSource.getRepository(fakeRepository.id).first()
        assertEquals(repositories?.isStarred, false)
    }

    @Test
    fun Star실행후_성공결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )

        // when
        val result = repoRepository.starRepository("test", "test")

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun Star실행_IOExcepion발생후_실패결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource(IOException())
        )

        // when
        val result = repoRepository.starRepository("test", "test")

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun Star실행_401에러발생후_실패결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource(
                HttpException(Response.error<Any>(401, "empty body".toResponseBody()))
            )
        )

        // when
        val result = repoRepository.starRepository("test", "test")

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun Star실행_404에러발생후_실패결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource(
                HttpException(Response.error<Any>(404, "empty body".toResponseBody()))
            )
        )

        // when
        val result = repoRepository.starRepository("test", "test")

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RepositoryException.NotFoundRepository)
    }

    @Test
    fun Star실행_UnKnown에러발생후_실패결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource(
                CommonException.UnKnownError()
            )
        )

        // when
        val result = repoRepository.starRepository("test", "test")

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.UnKnownError)
    }

    @Test
    fun unStar실행후_성공결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )

        // when
        val result = repoRepository.unStarRepository("test", "test")

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun unStar실행_IOExcepion발생후_실패결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource(IOException())
        )

        // when
        val result = repoRepository.unStarRepository("test", "test")

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun unStar실행_401에러발생후_실패결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource(
                HttpException(Response.error<Any>(401, "empty body".toResponseBody()))
            )
        )

        // when
        val result = repoRepository.unStarRepository("test", "test")

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.AuthorizationError)
    }

    @Test
    fun unStar실행_404에러발생후_실패결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource(
                HttpException(Response.error<Any>(404, "empty body".toResponseBody()))
            )
        )

        // when
        val result = repoRepository.unStarRepository("test", "test")

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RepositoryException.NotFoundRepository)
    }

    @Test
    fun unStar실행_UnKnown에러발생후_실패결과_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource(
                CommonException.UnKnownError()
            )
        )

        // when
        val result = repoRepository.unStarRepository("test", "test")

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.UnKnownError)
    }

    @Test
    fun 유효한ID로_Star후_스타상태및카운트_업데이트() = runTest {
        // given
        val fakeRepositories = makeFakeRepositories(1, 10)
        val index = fakeRepositories.indices.first
        val fakeRepository = fakeRepositories[index]
        val expectedIsStarred = true
        val expectedStarCount = fakeRepository.stargazersCount + 1
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )
        repositoryLocalDataSource.insertRepositories(fakeRepositories.map { it.toModel().toLocalModel() })

        // when
        repoRepository.starLocalRepository(fakeRepository.id, fakeRepository.stargazersCount + 1)

        // then
        val roomRepository = repositoryLocalDataSource.getRepository(fakeRepository.id).first()
        assertEquals(roomRepository?.isStarred, expectedIsStarred)
        assertEquals(roomRepository?.stargazersCount, expectedStarCount)
    }

    @Test
    fun 유효한ID로_unStar후_스타상태및카운트_업데이트() = runTest {
        // given
        val fakeRepositories = makeFakeRepositories(1, 10)
        val index = fakeRepositories.indices.first
        val fakeRepository = fakeRepositories[index]
        val expectedIsStarred = false
        val expectedStarCount = fakeRepository.stargazersCount
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )
        repositoryLocalDataSource.insertRepositories(fakeRepositories.map { it.toModel().toLocalModel() })

        // when
        repoRepository.unStarLocalRepository(fakeRepository.id, fakeRepository.stargazersCount)

        // then
        val roomRepository = repositoryLocalDataSource.getRepository(fakeRepository.id).first()
        assertEquals(roomRepository?.isStarred, expectedIsStarred)
        assertEquals(roomRepository?.stargazersCount, expectedStarCount)
    }

    @Test
    fun 스타카운트_변경없을때_레파지토리상세_반환() = runTest {
        // given
        val fakeRepositories = makeFakeRepositories(1, 10)
        val index = fakeRepositories.indices.first
        val fakeRepository = fakeRepositories[index]
        val expectedRepositoryDetail =
            RepositoryDetail(
                id = fakeRepository.id,
                name = fakeRepository.name,
                owner = fakeRepository.owner.toModel(),
                stargazersCount = fakeRepository.stargazersCount,
                forksCount = 0,
                isStarred = null,
                issueCount = 10,
                pullCount = 10,
                readme = "hello world!"
            )
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(
                repositories = fakeRepositories,
                issueCount = expectedRepositoryDetail.issueCount,
                pullCount = expectedRepositoryDetail.pullCount,
                readme = expectedRepositoryDetail.readme
            ),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )

        // when
        val result = repoRepository.getRepository(fakeRepository.owner.login, fakeRepository.name)

        // then
        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), expectedRepositoryDetail)
    }

    @Test
    fun 스타카운트_변경됐을때_레파지토리상세_반환() = runTest {
        // given
        val fakeRepositories = makeFakeRepositories(1, 10)
        val index = fakeRepositories.indices.first
        val fakeRepository = fakeRepositories[index]
        val expectedRepositoryDetail =
            RepositoryDetail(
                id = fakeRepository.id,
                name = fakeRepository.name,
                owner = fakeRepository.owner.toModel(),
                stargazersCount = 20,
                forksCount = 0,
                isStarred = null,
                issueCount = 10,
                pullCount = 10,
                readme = "hello world!"
            )
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(
                repositories = fakeRepositories,
                starCount = expectedRepositoryDetail.stargazersCount,
                issueCount = expectedRepositoryDetail.issueCount,
                pullCount = expectedRepositoryDetail.pullCount,
                readme = expectedRepositoryDetail.readme
            ),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )

        // when
        val result = repoRepository.getRepository(fakeRepository.owner.login, fakeRepository.name)

        // then
        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), expectedRepositoryDetail)
    }

    @Test
    fun 레자피토리상세_IOException_실패_반환() = runTest {
        // given
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(
                throwable = IOException()
            ),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )

        // when
        val result = repoRepository.getRepository("test", "test")

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is CommonException.NetworkError)
    }

    @Test
    fun 레파지토리_삭제_빈배열_반환() = runTest {
        val fakeRepositories = makeFakeRepositories(1, 10)
        initialRepoRepository(
            repoApiDataSource = FakeRepoApiDataSource(),
            repoStarApiDataSource = FakeRepoStarApiDataSource()
        )
        repositoryLocalDataSource.insertRepositories(fakeRepositories.map { it.toModel().toLocalModel() })

        repoRepository.clearRepositories()

        val repositories = repositoryLocalDataSource.getRepositories().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageLoadSize,
                placeholdersEnabled = false
            )
        )
        val data = (repositories as? PagingSource.LoadResult.Page)?.data
        assertEquals(data?.size, 0)
    }

    private fun initialRepoRepository(
        repoApiDataSource: RepoApiDataSource,
        repoStarApiDataSource: RepoStarApiDataSource
    ) {
        this.repoApiDataSource = repoApiDataSource
        this.repoStarApiDataSource = repoStarApiDataSource
        repositoryLocalDataSource = FakeRepositoryLocalDataSource()
        remoteKeyLocalDataSource = FakeRemoteKeyLocalDataSource()

        repoRepository = RepoRepositoryImpl(repoApiDataSource, repoStarApiDataSource, repositoryLocalDataSource, remoteKeyLocalDataSource)
    }

    private fun makeFakeRepositories(page : Int, loadSize: Int) : List<RepositoryResponse> =
        List(loadSize) { index ->
            val id = index + (10 * page)
            RepositoryResponse(
                id = id,
                name = "test $id",
                owner = OwnerResponse(
                    login = "login $id",
                    avatarUrl = "avatarUrl $id"
                ),
                stargazersCount = 0,
                defaultBranch = "master",
                updatedAt = "2022-01-01"
            )
        }

    private suspend fun validateRepositoriesInDatabase(
        fakeRepositories: List<RepositoryResponse>,
        expectedPrevKey: Int?,
        expectedNextKey: Int?,
        params: PagingSource.LoadParams<Int>
    ) {
        val repositories = repositoryLocalDataSource
            .getRepositories()
            .load(params)
        val data = (repositories as? PagingSource.LoadResult.Page)?.data
        assertEquals(data?.size, fakeRepositories.size)
        data?.indices?.forEach { index ->
            val expectedRepository = fakeRepositories[index]
            val remoteKey = remoteKeyLocalDataSource.remoteKey(expectedRepository.id)
            assertEquals(data[index], expectedRepository.toModel().toLocalModel())
            assertEquals(remoteKey?.repoId, expectedRepository.id)
            assertEquals(remoteKey?.prevKey, expectedPrevKey)
            assertEquals(remoteKey?.nextKey, expectedNextKey)
        }
    }
}