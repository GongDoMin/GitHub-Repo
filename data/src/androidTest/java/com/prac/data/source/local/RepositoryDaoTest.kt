package com.prac.data.source.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prac.data.source.local.room.dao.RepositoryDao
import com.prac.data.source.local.room.database.RepositoryDatabase
import org.junit.After
import org.junit.Before
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
}