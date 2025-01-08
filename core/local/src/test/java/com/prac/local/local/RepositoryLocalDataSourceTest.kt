package com.prac.local.local

import androidx.paging.PagingSource
import com.prac.local.RepositoryLocalDataSource
import com.prac.local.impl.RepositoryLocalDataSourceImpl
import com.prac.local.model.OwnerEntity
import com.prac.local.model.RepositoryEntity
import com.prac.local.room.dao.RepositoryDao
import com.prac.shared_test.local.room.FakeRepositoryDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoryLocalDataSourceTest {

    private val repositoryDao: RepositoryDao = FakeRepositoryDao()
    private val repositoryLocalDataSource: RepositoryLocalDataSource = RepositoryLocalDataSourceImpl(repositoryDao)

    @After
    fun 정리() = runTest {
        repositoryDao.clearRepositories()
    }

    @Test
    fun 레파지토리_2개_저장후_조회() = runTest {
        // when
        repositoryLocalDataSource.insertRepositories(fakeRepositories)

        // then
        val repositories =
            repositoryLocalDataSource
                .getRepositories()
                .load(
                    PagingSource
                        .LoadParams
                        .Refresh(
                            key = null,
                            loadSize = 10,
                            placeholdersEnabled = false
                        )
                )
        val result = (repositories as? PagingSource.LoadResult.Page)?.data
        assertEquals(result, fakeRepositories)
    }

    @Test
    fun 비어있다면_빈리스트_반환() = runTest {

        // when
        val repositories =
            repositoryLocalDataSource
                .getRepositories()
                .load(
                    PagingSource
                        .LoadParams
                        .Refresh(
                            key = null,
                            loadSize = 10,
                            placeholdersEnabled = false
                        )
                )

        // then
        val result = (repositories as? PagingSource.LoadResult.Page)?.data
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun 존재하는_레파지토리_조회후_레파지토리_반환() = runTest {
        // given
        val index = fakeRepositories.size - 1
        val expectedRepository = fakeRepositories[index]
        repositoryLocalDataSource.insertRepositories(fakeRepositories)

        // when
        val result = repositoryLocalDataSource.getRepository(expectedRepository.id).first()

        // then
        assertEquals(result, expectedRepository)
    }

    @Test
    fun 존재하는않는_레파지토리_조회후_널_반환() = runTest {
        // given
        val id = fakeRepositories.maxOf { it.id } + 1
        repositoryLocalDataSource.insertRepositories(fakeRepositories)

        // when
        val result = repositoryLocalDataSource.getRepository(id).first()

        // then
        assertNull(result)
    }

    @Test
    fun 별상태와카운트_업데이트_정상적으로_업데이트() = runTest {
        // given
        val index = fakeRepositories.indices.first
        val id = fakeRepositories[index].id
        val expectedIsStarred = true
        val expectedUpdatedCount = 1
        repositoryLocalDataSource.insertRepositories(fakeRepositories)

        // when
        repositoryLocalDataSource.updateStarStateAndStarCount(id, expectedIsStarred, expectedUpdatedCount)

        // then
        val result = repositoryLocalDataSource.getRepository(id).first()
        assertEquals(result?.isStarred, expectedIsStarred)
        assertEquals(result?.stargazersCount, expectedUpdatedCount)
    }

    @Test
    fun 별상태_업데이트_정상적으로_업데이트() = runTest {
        // given
        val index = fakeRepositories.indices.first
        val id = fakeRepositories[index].id
        val expectedIsStarred = true
        repositoryLocalDataSource.insertRepositories(fakeRepositories)

        // when
        repositoryLocalDataSource.updateStarState(id, expectedIsStarred)

        // then
        val result = repositoryLocalDataSource.getRepository(id).first()
        assertEquals(result?.isStarred, expectedIsStarred)
    }

    @Test
    fun 별카운드_업데이트_정상적으로_업데이트() = runTest {
        // given
        val index = fakeRepositories.indices.first
        val id = fakeRepositories[index].id
        val expectedUpdatedCount = 1
        repositoryLocalDataSource.insertRepositories(fakeRepositories)

        // when
        repositoryLocalDataSource.updateStarCount(id, expectedUpdatedCount)

        // then
        val updatedRepository = repositoryLocalDataSource.getRepository(id).first()
        assertEquals(updatedRepository?.stargazersCount, expectedUpdatedCount)
    }

    @Test
    fun 초기화_빈리스트_반환() = runTest {
        // given
        val expectedSize = 0
        repositoryLocalDataSource.insertRepositories(fakeRepositories)

        // when
        repositoryLocalDataSource.clearRepositories()

        // then
        val repositories =
            repositoryLocalDataSource
                .getRepositories()
                .load(
                    PagingSource
                        .LoadParams
                        .Refresh(
                            key = null,
                            loadSize = 10,
                            placeholdersEnabled = false
                        )
                )

        val result = (repositories as? PagingSource.LoadResult.Page)?.data
        assertEquals(result?.size, expectedSize)
    }

    companion object {
        private val fakeRepositories =
            listOf(
                RepositoryEntity(1, "Repository 1", OwnerEntity("login 1", "avatar 1"), 0, "2023.01.05", "master", false),
                RepositoryEntity(2, "Repository 2", OwnerEntity("login 2", "avatar 2"), 0, "2023.01.05", "master", false)
            )
    }
}
