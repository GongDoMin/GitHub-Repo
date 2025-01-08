package com.prac.feature.main

import com.prac.feature.main.model.Mutation
import com.prac.feature.main.refresh.RefreshState
import com.prac.feature.main.view.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainReducerProcessorTest {

    private val mainReducerProcessorTest = MainReducerProcessor()

    @Test
    fun 컨텐트상태_변환테스트() {
        // when
        val result = mainReducerProcessorTest.invoke(Mutation.ShowContent, UiState.Error())

        // then
        assertTrue(result is UiState.Content)
    }

    @Test
    fun 에러상태_변환테스트() {
        // given
        val expectedErrorMessage = "test"

        // when
        val result = mainReducerProcessorTest.invoke(Mutation.ShowError(expectedErrorMessage), UiState.Content())

        // then
        assertTrue(result is UiState.Error)
        assertEquals((result as UiState.Error).message, expectedErrorMessage)
    }

    @Test
    fun 리플래시상태_변환테스트() {
        // when
        val result = mainReducerProcessorTest.invoke(Mutation.UpdateRefreshState(RefreshState.PullingDown), UiState.Content())

        // then
        assertTrue(result is UiState.Content)
        assertEquals((result as UiState.Content).refreshState, RefreshState.PullingDown)
    }
}