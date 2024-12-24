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

        val result = detailReducerProcessorTest.invoke(Mutation.ShowLoading, UiState.Loading)

        assertTrue(result is UiState.Loading)
    }

    @Test
    fun invoke_mutationIsShowError_uiStateIsError() {
        val errorMessage = "test"

        val result = detailReducerProcessorTest.invoke(Mutation.ShowError(errorMessage), UiState.Loading)

        assertTrue(result is UiState.Error)
        assertEquals((result as UiState.Error).message, errorMessage)
    }

    @Test
    fun invoke_mutationIsShowRepository_uiStateIsShowRepository() {
        val repository = RepoDetailModel()

        val result = detailReducerProcessorTest.invoke(Mutation.ShowRepository(repository), UiState.Loading)

        assertEquals((result as UiState.Content), repository)
    }
}