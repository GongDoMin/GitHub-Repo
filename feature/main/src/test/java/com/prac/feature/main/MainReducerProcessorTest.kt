package com.prac.feature.main

import com.prac.feature.main.model.Mutation
import com.prac.feature.main.view.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainReducerProcessorTest {

    private val mainReducerProcessorTest = MainReducerProcessor()

    @Test
    fun invoke_mutationIsShowRepositories_uiStateIsShowRepositories() {
        val result = mainReducerProcessorTest.invoke(Mutation.ShowContent, UiState())

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
}