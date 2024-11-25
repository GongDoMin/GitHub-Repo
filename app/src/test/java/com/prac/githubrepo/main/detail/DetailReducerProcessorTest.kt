package com.prac.githubrepo.main.detail

import com.prac.data.entity.RepoDetailEntity
import com.prac.githubrepo.ui.home.main.detail.DetailReducerProcessor
import com.prac.githubrepo.ui.home.main.detail.model.Mutation
import com.prac.githubrepo.ui.home.main.detail.view.UiState
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DetailReducerProcessorTest {

    private val detailReducerProcessorTest = DetailReducerProcessor()

    @Test
    fun invoke_mutationIsShowLoading_uiStateIsLoading() {

        val result = detailReducerProcessorTest.invoke(Mutation.ShowLoading, UiState())

        assertTrue(result.isLoading)
    }

    @Test
    fun invoke_mutationIsShowError_uiStateIsError() {
        val errorMessage = "test"

        val result = detailReducerProcessorTest.invoke(Mutation.ShowError(errorMessage), UiState())

        assertTrue(result.isError)
        assertEquals(result.errorMessage, errorMessage)
    }

    @Test
    fun invoke_mutationIsShowRepository_uiStateIsShowRepository() {
        val repository = RepoDetailEntity()

        val result = detailReducerProcessorTest.invoke(Mutation.ShowRepository(repository), UiState())

        assertEquals(result.repository, repository)
        assertFalse(result.isLoading)
        assertFalse(result.isError)
        assertTrue(result.errorMessage.isEmpty())
    }

    @Test
    fun invoke_mutationIsDialogDismiss_uiStateIsShowRepository() {
        val repository = RepoDetailEntity()

        val result = detailReducerProcessorTest.invoke(Mutation.DismissError, UiState(repository = repository))

        assertEquals(result.repository, repository)
        assertFalse(result.isLoading)
        assertFalse(result.isError)
        assertTrue(result.errorMessage.isEmpty())
    }
}