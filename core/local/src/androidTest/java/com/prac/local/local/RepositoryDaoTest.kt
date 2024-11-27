package com.prac.local.local

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prac.local.room.dao.RepositoryDao
import com.prac.local.room.database.RepositoryDatabase
import com.prac.local.room.entity.Owner
import com.prac.local.room.entity.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryDaoTest {

    private lateinit var repositoryDatabase: RepositoryDatabase
    private lateinit var repositoryDao: RepositoryDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        repositoryDatabase = Room.inMemoryDatabaseBuilder(context, RepositoryDatabase::class.java)
            .build()
        repositoryDao = repositoryDatabase.repositoryDao()
    }

    @After
    fun tearDown() {
        repositoryDatabase.close()
    }

    @Test
    fun getRepositories_roomIsEmpty_emptyList() = runTest {

        val result = (repositoryDao.getRepositories().load(
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

        repositoryDao.insertRepositories(repositories)

        val result = (repositoryDao.getRepositories().load(
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
        repositoryDao.insertRepositories(repositories)
        val index = 0
        val id = repositories[index].id

        val result = repositoryDao.getRepository(id).first()

        assertEquals(repositories[index], result)
    }

    @Test
    fun getRepository_notExistingID_null() = runTest {
        val repositories = makeRepositories()
        repositoryDao.insertRepositories(repositories)
        val id = repositories.maxOf { it.id } + 1 // 존재하지 않는 아이디

        val result = repositoryDao.getRepository(id).first()

        assertNull(result)
    }

    @Test
    fun updateStarStateAndStarCount_existingID_updateStarStateAndStateCountCorrectly() = runTest {
        val repositories = makeRepositories()
        repositoryDao.insertRepositories(repositories)
        val index = 0
        val id = repositories[index].id
        val isStarred = true
        val updatedCount = 1

        repositoryDao.updateStarStateAndStarCount(id, isStarred, updatedCount)

        val result = repositoryDao.getRepository(id).first()
        assertEquals(result?.isStarred, isStarred)
        assertEquals(result?.stargazersCount, updatedCount)
    }

    @Test
    fun updateStarState_existingID_updateStarStateCorrectly() = runTest {
        val repositories = makeRepositories()
        repositoryDao.insertRepositories(repositories)
        val index = 0
        val id = repositories[index].id
        val isStarred = true

        repositoryDao.updateStarState(id, isStarred)

        val result = repositoryDao.getRepository(id).first()
        assertEquals(result?.isStarred, isStarred)
    }

    @Test
    fun updateStarCount_existingID_updateStarCountCorrectly() = runTest {
        val repositories = makeRepositories()
        repositoryDao.insertRepositories(repositories)
        val index = 0
        val id = repositories[index].id
        val updatedCount = 1

        repositoryDao.updateStarCount(id, updatedCount)

        val updatedRepository = repositoryDao.getRepository(id).first()
        assertEquals(updatedRepository?.stargazersCount, updatedCount)
    }

    @Test
    fun clearRepositories_clearRoom_emptyList() = runTest {
        val repositories = makeRepositories()
        repositoryDao.insertRepositories(repositories)

        repositoryDao.clearRepositories()

        val result = (repositoryDao.getRepositories().load(
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