package com.prac.feature.detail

import com.prac.data.entity.RepoDetailEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DetailReducerProcessorTest {

    private val detailReducerProcessorTest = DetailReducerProcessor()

    @Test
    fun invoke_mutationIsShowLoading_uiStateIsLoading() {

        val result = detailReducerProcessorTest.invoke(
            com.prac.feature.detail.model.Mutation.ShowLoading,
            com.prac.feature.detail.view.UiState()
        )

        assertTrue(result.isLoading)
    }

    @Test
    fun invoke_mutationIsShowError_uiStateIsError() {
        val errorMessage = "test"

        val result = detailReducerProcessorTest.invoke(
            com.prac.feature.detail.model.Mutation.ShowError(errorMessage),
            com.prac.feature.detail.view.UiState()
        )

        assertTrue(result.isError)
        assertEquals(result.errorMessage, errorMessage)
    }

    @Test
    fun invoke_mutationIsShowRepository_uiStateIsShowRepository() {
        val repository = RepoDetailEntity()

        val result = detailReducerProcessorTest.invoke(
            com.prac.feature.detail.model.Mutation.ShowRepository(repository),
            com.prac.feature.detail.view.UiState()
        )

        assertEquals(result.repository, repository)
        assertFalse(result.isLoading)
        assertFalse(result.isError)
        assertTrue(result.errorMessage.isEmpty())
    }

    @Test
    fun invoke_mutationIsDialogDismiss_uiStateIsShowRepository() {
        val repository = RepoDetailEntity()

        val result = detailReducerProcessorTest.invoke(
            com.prac.feature.detail.model.Mutation.DismissError,
            com.prac.feature.detail.view.UiState(repository = repository)
        )

        assertEquals(result.repository, repository)
        assertFalse(result.isLoading)
        assertFalse(result.isError)
        assertTrue(result.errorMessage.isEmpty())
    }
}