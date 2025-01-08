package com.prac.feature.profile

import app.cash.turbine.test
import com.prac.feature.profile.model.Action
import com.prac.feature.profile.model.Event
import com.prac.feature.profile.model.Mutation
import com.prac.shared_test.common.FakeBackOffWorkManager
import com.prac.shared_test.domain.FakeClearLocalDataUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProfileActionProcessorTest {

    private lateinit var clearLocalDataUseCase: FakeClearLocalDataUseCase
    private val backOffWork: FakeBackOffWorkManager = FakeBackOffWorkManager()

    private lateinit var profileActionProcessor: ProfileActionProcessor

    @Before
    fun setUp() {
        initialProfileActionProcessor()
    }

    @Test
    fun 로그아웃버튼클릭_액션발행_mutation은_ShowDialog() = runTest {
        // when, then
        profileActionProcessor(Action.UserAction.OnClickLogoutButton).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowDialog)
            assertTrue(event == null)
        }
    }

    @Test
    fun 다이어로그해제_액션발행_mutation은_ShowIdle() = runTest {
        // when, then
        profileActionProcessor(Action.UserAction.DialogDismiss).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowIdle)
            assertTrue(event == null)
        }
    }

    @Test
    fun 다이어로드취소버튼클릭_액션발행_mutation은_ShowIdle() = runTest {
        // when, then
        profileActionProcessor(Action.UserAction.OnClickNegativeButton).test {
            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation is Mutation.ShowIdle)
            assertTrue(event == null)
        }
    }

    @Test
    fun 다이어로드확인버튼클릭_액션발행_event는_Logout() = runTest {
        // when, then
        profileActionProcessor(Action.UserAction.OnClickPositiveButton).test {
            awaitItem() // isLoading

            val (mutation, event) = awaitItem()
            awaitComplete()
            assertTrue(mutation == null)
            assertTrue(event is Event.Logout)

            assertTrue(backOffWork.getWorkSize() == 0)
            assertTrue(clearLocalDataUseCase.isCleared())
        }
    }

    private fun initialProfileActionProcessor() {
        clearLocalDataUseCase = FakeClearLocalDataUseCase(
            userName = "kane",
            accessToken = "token",
            list = listOf("list1", "list2")
        )
        profileActionProcessor = ProfileActionProcessor(
            clearLocalDataUseCase = clearLocalDataUseCase,
            backOffWorkManager = backOffWork
        )

    }
}