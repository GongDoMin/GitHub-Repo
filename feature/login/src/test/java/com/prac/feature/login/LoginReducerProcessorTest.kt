package com.prac.feature.login

import com.prac.feature.login.model.Mutation
import com.prac.feature.login.view.UiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginReducerProcessorTest {

    private val loginReducerProcessorTest = LoginReducerProcessor()

    @Test
    fun 아이들상태_변환테스트() {
        // when
        val result = loginReducerProcessorTest.invoke(
            Mutation.ShowIdle,
            UiState.Loading
        )

        // then
        assertFalse(result is UiState.Loading)
        assertFalse(result is UiState.Error)
    }

    @Test
    fun 로딩상태_변환테스트() {
        // when
        val result = loginReducerProcessorTest.invoke(
            Mutation.ShowLoading,
            UiState.Idle
        )

        // then
        assertTrue(result is UiState.Loading)
        assertFalse(result is UiState.Error)
    }

    @Test
    fun 에러상태_변환테스트() {
        // when
        val result = loginReducerProcessorTest.invoke(
            Mutation.ShowError("test"),
            UiState.Idle
        )

        // then
        assertFalse(result is UiState.Loading)
        assertTrue(result is UiState.Error)
    }
}