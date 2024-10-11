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
}