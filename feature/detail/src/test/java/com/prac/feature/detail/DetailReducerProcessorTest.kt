package com.prac.feature.detail

import com.prac.data.model.RepoDetailModel
import com.prac.feature.detail.model.Mutation
import com.prac.feature.detail.view.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DetailReducerProcessorTest {

    private val detailReducerProcessorTest = DetailReducerProcessor()

    @Test
    fun invoke_mutationIsShowLoading_uiStateIsLoading() {

        val result = detailReducerProcessorTest.invoke(
            Mutation.ShowLoading,
            UiState()
        )

        assertTrue(result.isLoading)
    }

    @Test
    fun invoke_mutationIsShowError_uiStateIsError() {
        val errorMessage = "test"

        val result = detailReducerProcessorTest.invoke(
            Mutation.ShowError(errorMessage),
            UiState()
        )

        assertTrue(result.isError)
        assertEquals(result.errorMessage, errorMessage)
    }

    @Test
    fun invoke_mutationIsShowRepository_uiStateIsShowRepository() {
        val repository = RepoDetailModel()

        val result = detailReducerProcessorTest.invoke(
            Mutation.ShowRepository(repository),
            UiState()
        )

        assertEquals(result.repository, repository)
        assertFalse(result.isLoading)
        assertFalse(result.isError)
        assertTrue(result.errorMessage.isEmpty())
    }

    @Test
    fun invoke_mutationIsDialogDismiss_uiStateIsShowRepository() {
        val repository = RepoDetailModel()

        val result = detailReducerProcessorTest.invoke(
            Mutation.DismissError,
            UiState(repository = repository)
        )

        assertEquals(result.repository, repository)
        assertFalse(result.isLoading)
        assertFalse(result.isError)
        assertTrue(result.errorMessage.isEmpty())
    }
}