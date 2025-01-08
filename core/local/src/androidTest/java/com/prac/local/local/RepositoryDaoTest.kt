package com.prac.local.local

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prac.local.model.OwnerEntity
import com.prac.local.model.RepositoryEntity
import com.prac.local.room.dao.RepositoryDao
import com.prac.local.room.database.RepositoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random
import kotlin.random.nextInt

@RunWith(AndroidJUnit4::class)
class RepositoryDaoTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var repositoryDatabase: RepositoryDatabase
    private lateinit var repositoryDao: RepositoryDao

    @Before
    fun 초기화() {
        repositoryDatabase = Room
            .inMemoryDatabaseBuilder(context, RepositoryDatabase::class.java)
            .build()
        repositoryDao = repositoryDatabase.repositoryDao()
    }

    @After
    fun 정리() = runTest {
        repositoryDao.clearRepositories()
        repositoryDatabase.close()
    }

    @Test
    fun 룸이_비어있다면_빈리스트_반환() = runTest {
        // when
        val repositories =
            repositoryDao
                .getRepositories()
                .load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = 10,
                        placeholdersEnabled = false
                    )
                )
        val result =  (repositories as? PagingSource.LoadResult.Page)?.data

        // then
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun 레파지토리_2개_저장후_룸조회() = runTest {

        // when
        repositoryDao.insertRepositories(fakeRepositories)

        // then
        val repositories =
            repositoryDao
                .getRepositories()
                .load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = 10,
                        placeholdersEnabled = false
                    )
                )
        val result = (repositories as? PagingSource.LoadResult.Page)?.data
        assertEquals(result, fakeRepositories)
    }

    @Test
    fun 존재하는_레파지토리_조회후_레파지토리_반환() = runTest {
        // given
        val index = fakeRepositories.size - 1
        val id = fakeRepositories[index].id
        repositoryDao.insertRepositories(fakeRepositories)

        // when
        val result = repositoryDao.getRepository(id).first()

        // then
        assertEquals(result, fakeRepositories[index])
    }

    @Test
    fun 존재하는않는_레파지토리_조회후_널_반환() = runTest {
        // given
        val randomID = Random.nextInt(IntRange(100, 1000))
        repositoryDao.insertRepositories(fakeRepositories)

        // when
        val result = repositoryDao.getRepository(randomID).first()

        // then
        assertNull(result)
    }

    @Test
    fun 별상태와카운트_업데이트_정상적으로_업데이트() = runTest {
        // given
        val index = fakeRepositories.size - 1
        val id = fakeRepositories[index].id
        val expectedIsStarred = true
        val expectedUpdatedCount = 1
        repositoryDao.insertRepositories(fakeRepositories)

        // when
        repositoryDao.updateStarStateAndStarCount(id, expectedIsStarred, expectedUpdatedCount)

        // then
        val result = repositoryDao.getRepository(id).first()
        assertEquals(result?.isStarred, expectedIsStarred)
        assertEquals(result?.stargazersCount, expectedUpdatedCount)
    }

    @Test
    fun 별상태_업데이트_정상적으로_업데이트() = runTest {
        // given
        val index = fakeRepositories.size - 1
        val id = fakeRepositories[index].id
        val expectedIsStarred = true
        repositoryDao.insertRepositories(fakeRepositories)

        // when
        repositoryDao.updateStarState(id, expectedIsStarred)

        // then
        val result = repositoryDao.getRepository(id).first()
        assertEquals(result?.isStarred, expectedIsStarred)
    }

    @Test
    fun 별카운드_업데이트_정상적으로_업데이트() = runTest {
        // given
        val index = fakeRepositories.size - 1
        val id = fakeRepositories[index].id
        val expectedUpdatedCount = true
        repositoryDao.insertRepositories(fakeRepositories)

        // when
        repositoryDao.updateStarState(id, expectedUpdatedCount)

        // then
        val result = repositoryDao.getRepository(id).first()
        assertEquals(result?.isStarred, expectedUpdatedCount)
    }

    @Test
    fun 룸_초기화_빈리스트_반환() = runTest {
        // given
        val expectedSize = 0
        repositoryDao.insertRepositories(fakeRepositories)

        // when
        repositoryDao.clearRepositories()

        // then
        val repositories =
            repositoryDao
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