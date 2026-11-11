package com.example.courtgate.ui

import androidx.annotation.StringRes
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.platform.app.InstrumentationRegistry

const val UI_TIMEOUT_MS = 5_000L

// Los textos se leen de los recursos: los tests valen con el dispositivo en español o en inglés
fun str(@StringRes id: Int, vararg args: Any): String =
    InstrumentationRegistry.getInstrumentation().targetContext.getString(id, *args)

@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.waitForText(text: String) =
    waitUntilAtLeastOneExists(hasText(text), UI_TIMEOUT_MS)
