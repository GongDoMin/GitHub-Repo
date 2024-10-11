package com.prac.data.source.local

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prac.data.source.local.room.dao.RepositoryDao
import com.prac.data.source.local.room.database.RepositoryDatabase
import com.prac.data.source.local.room.entity.Owner
import com.prac.data.source.local.room.entity.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun insertRepositories_insertItemsIntoDatabase() = runTest {
        val repositories = makeRepositories()

        repositoryDao.insertRepositories(repositories)

        val result = (repositoryDao.getRepositories().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 10, placeholdersEnabled = false)
        ) as? PagingSource.LoadResult.Page)?.data
        assertEquals(result, repositories)
    }

    @Test
    fun getRepository_withExistingId_returnsRepository() = runTest {
        val repositories = makeRepositories()
        val index = 0
        val id = repositories[index].id
        repositoryDao.insertRepositories(repositories)

        val result = repositoryDao.getRepository(id).first()

        assertEquals(repositories[index], result)
    }

    @Test
    fun getRepository_withNonExistingId_returnsNull() = runTest {
        val repositories = makeRepositories()
        val id = repositories.maxOf { it.id } + 1
        repositoryDao.insertRepositories(repositories)

        val result = repositoryDao.getRepository(id).first()

        assertNull(result)
    }

    private fun makeRepositories() =
        listOf(
            Repository(1, "repo1", Owner("test1", "test1"), 0, "test1", false),
            Repository(2, "repo2", Owner("test2", "test2"), 0, "test2", false)
        )
}