package com.prac.local.local

import androidx.paging.PagingSource
import com.prac.local.RepositoryLocalDataSource
import com.prac.local.impl.RepositoryLocalDataSourceImpl
import com.prac.local.room.entity.Owner
import com.prac.local.room.entity.Repository
import com.prac.shared_test.local.room.FakeRepositoryDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositoryLocalDataSourceTest {

    private lateinit var repositoryDao: FakeRepositoryDao
    private lateinit var repositoryLocalDataSource: RepositoryLocalDataSource

    @Before
    fun setUp() {
        repositoryDao = FakeRepositoryDao()
        repositoryLocalDataSource = RepositoryLocalDataSourceImpl(repositoryDao)
    }

    @Test
    fun getRepositories_roomIsEmpty_emptyList() = runTest {

        val result = (repositoryLocalDataSource.getRepositories().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        ) as? PagingSource.LoadResult.Page)?.data

        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun insertRepositories_insertTwoRepositories_twoRepositories() = runTest {
        val repositories = makeRepositories()
        val expectedSize = 2

        repositoryLocalDataSource.insertRepositories(repositories)

        val result = (repositoryLocalDataSource.getRepositories().load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        ) as? PagingSource.LoadResult.Page)?.data
        assertEquals(result?.size, expectedSize)
        assertEquals(result, repositories)
    }

    @Test
    fun getRepository_existingID_repository() = runTest {
        val repositories = makeRepositories()
        repositoryLocalDataSource.insertRepositories(repositories)
        val index = 0
        val id = repositories[index].id

        val result = repositoryLocalDataSource.getRepository(id).first()

        assertEquals(repositories[index], result)
    }

    @Test
    fun getRepository_notExistingID_null() = runTest {
        val repositories = makeRepositories()
        repositoryLocalDataSource.insertRepositories(repositories)
        val id = repositories.maxOf { it.id } + 1 // 존재하지 않는 아이디

        val result = repositoryLocalDataSource.getRepository(id).first()

        assertNull(result)
    }

    @Test
    fun updateStarStateAndStarCount_existingID_updateStarStateAndStateCountCorrectly() = runTest {
        val repositories = makeRepositories()
        repositoryLocalDataSource.insertRepositories(repositories)
        val index = 0
        val id = repositories[index].id
        val isStarred = true
        val updatedCount = 1

        repositoryLocalDataSource.updateStarStateAndStarCount(id, isStarred, updatedCount)

        val result = repositoryLocalDataSource.getRepository(id).first()
        assertEquals(result?.isStarred, isStarred)
        assertEquals(result?.stargazersCount, updatedCount)
    }

    @Test
    fun updateStarState_existingID_updateStarStateCorrectly() = runTest {
        val repositories = makeRepositories()
        repositoryLocalDataSource.insertRepositories(repositories)
        val index = 0
        val id = repositories[index].id
        val isStarred = true

        repositoryLocalDataSource.updateStarState(id, isStarred)

        val result = repositoryLocalDataSource.getRepository(id).first()
        assertEquals(result?.isStarred, isStarred)
    }

    @Test
    fun updateStarCount_existingID_updateStarCountCorrectly() = runTest {
        val repositories = makeRepositories()
        repositoryLocalDataSource.insertRepositories(repositories)
        val index = 0
        val id = repositories[index].id
        val updatedCount = 1

        repositoryLocalDataSource.updateStarCount(id, updatedCount)

        val updatedRepository = repositoryLocalDataSource.getRepository(id).first()
        assertEquals(updatedRepository?.stargazersCount, updatedCount)
    }

    @Test
    fun clearRepositories_clearRoom_emptyList() = runTest {
        val repositories = makeRepositories()
        repositoryLocalDataSource.insertRepositories(repositories)

        repositoryLocalDataSource.clearRepositories()

        val result = (repositoryLocalDataSource.getRepositories().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        ) as? PagingSource.LoadResult.Page)?.data
        assertEquals(result?.size, 0)
    }

    private fun makeRepositories() =
        listOf(
            Repository(1, "repo1", Owner("test1", "test1"), 0, "test1", "master", false),
            Repository(2, "repo2", Owner("test2", "test2"), 0, "test2", "master", false)
        )
}