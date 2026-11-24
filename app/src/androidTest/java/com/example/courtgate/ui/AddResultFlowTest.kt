package com.example.courtgate.ui

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import com.example.courtgate.MainActivity
import com.example.courtgate.R
import com.example.courtgate.fakes.FakeAuthDataSource
import com.example.courtgate.framework.database.LastResultDAO
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class AddResultFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var auth: FakeAuthDataSource

    // Room en memoria (InMemoryRoomModule)
    @Inject
    lateinit var dao: LastResultDAO

    @Before
    fun setUp() {
        hiltRule.inject()
        auth.logInAsTestUser()
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun addTwoSetResult_savesItInRoom() {
        composeRule.waitForText(str(R.string.nav_match))
        composeRule.onNodeWithText(str(R.string.nav_match)).performClick()
        composeRule.waitForText(str(R.string.match_empty))

        composeRule.onNodeWithContentDescription(str(R.string.match_add_result)).performClick()
        composeRule.waitForText(str(R.string.match_form_title_new))

        // Campos en orden: set 1 (pareja 1, pareja 2), set 2, set 3
        val fields = composeRule.onAllNodes(hasSetTextAction())
        listOf("6", "4", "6", "3").forEachIndexed { index, games ->
            fields[index].performTextInput(games)
        }
        composeRule.onNodeWithText(str(R.string.match_action_save)).performClick()

        composeRule.waitUntil(UI_TIMEOUT_MS) {
            composeRule.onAllNodesWithText(str(R.string.match_empty)).fetchSemanticsNodes().isEmpty()
        }
        val saved = runBlocking { dao.getLastResult().first() }.single()
        assertEquals(
            listOf(6, 4, 6, 3, 0, 0),
            with(saved) { listOf(player1Set1, player2Set1, player1Set2, player2Set2, player1Set3, player2Set3) }
        )
    }
}
