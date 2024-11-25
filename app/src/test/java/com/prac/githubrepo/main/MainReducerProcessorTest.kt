package com.prac.githubrepo.main

import androidx.paging.LoadState
import com.prac.data.entity.RepoEntity
import com.prac.githubrepo.ui.home.main.MainReducerProcessor
import com.prac.githubrepo.ui.home.main.model.Mutation
import com.prac.githubrepo.ui.home.main.view.UiState
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MainReducerProcessorTest {

    private val mainReducerProcessorTest = MainReducerProcessor()

    @Test
    fun invoke_mutationIsShowRepositories_uiStateIsShowRepositories() {
        val repositories = listOf(
            RepoEntity()
        )

        val result = mainReducerProcessorTest.invoke(Mutation.ShowRepositories, UiState(repositories = repositories))

        assertEquals(result.repositories, repositories)
        assertFalse(result.isError)
        assertTrue(result.errorMessage.isEmpty())
    }

    @Test
    fun invoke_mutationIsShowError_uiStateIsError() {
        val errorMessage = "test"

        val result = mainReducerProcessorTest.invoke(Mutation.ShowError(errorMessage), UiState())

        assertTrue(result.isError)
        assertEquals(result.errorMessage, errorMessage)
    }

    @Test
    fun invoke_mutationIsUpdateRepositories_uiStateIsShowRepositories() {
        val repositories = listOf(
            RepoEntity()
        )
        val loadState = LoadState.NotLoading(true)

        val result = mainReducerProcessorTest.invoke(Mutation.UpdateRepositories(repositories, loadState), UiState())

        assertEquals(result.repositories, repositories)
        assertEquals(result.loadState, loadState)
    }
}