package com.example.courtgate.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import com.example.courtgate.MainActivity
import com.example.courtgate.R
import com.example.courtgate.fakes.FakeAuthDataSource
import com.example.courtgate.fakes.FakeAuthDataSource.Companion.TEST_EMAIL
import com.example.courtgate.fakes.FakeAuthDataSource.Companion.TEST_PASSWORD
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class LoginFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var auth: FakeAuthDataSource

    @Before
    fun setUp() {
        hiltRule.inject()
        ActivityScenario.launch(MainActivity::class.java)
    }

    private fun fillForm(email: String, password: String) {
        composeRule.onNodeWithContentDescription(str(R.string.email_field_description))
            .performTextInput(email)
        composeRule.onNodeWithContentDescription(str(R.string.password_field_description))
            .performTextInput(password)
        composeRule.onNodeWithText(str(R.string.login_action)).performClick()
    }

    @Test
    fun validCredentials_openHome() {
        composeRule.waitForText(str(R.string.login_title))

        fillForm(TEST_EMAIL, TEST_PASSWORD)

        composeRule.waitForText(str(R.string.home_find_court))
        composeRule.onNodeWithText(str(R.string.home_find_court)).assertIsDisplayed()
        assertTrue(auth.isUserLoggedIn())
    }

    @Test
    fun wrongPassword_showsErrorAndStaysOnLogin() {
        composeRule.waitForText(str(R.string.login_title))

        fillForm(TEST_EMAIL, "Wrong1234")

        composeRule.waitForText(str(R.string.error_invalid_credentials))
        composeRule.onNodeWithText(str(R.string.login_title)).assertIsDisplayed()
        assertFalse(auth.isUserLoggedIn())
    }
}
