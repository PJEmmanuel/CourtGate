package com.example.courtgate.ui

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import com.example.courtgate.MainActivity
import com.example.courtgate.R
import com.example.courtgate.fakes.FakeAuthDataSource
import com.example.courtgate.fakes.FakeAuthDataSource.Companion.TEST_USER
import com.example.courtgate.fakes.FakeCourtRemoteDataSource
import com.example.courtgate.fakes.FakeCourtRemoteDataSource.Companion.TEST_COURT
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltAndroidTest
class BookingFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var auth: FakeAuthDataSource

    @Inject
    lateinit var remote: FakeCourtRemoteDataSource

    // Mañana: así las dos horas ofertadas son futuras sea cual sea la hora del test
    private val tomorrow = LocalDate.now().plusDays(1)

    @Before
    fun setUp() {
        hiltRule.inject()
        auth.logInAsTestUser()
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun bookFreeHour_confirmsAndSendsBooking() {
        composeRule.waitForText(str(R.string.home_find_court))
        composeRule.onNodeWithText(str(R.string.home_find_court)).performClick()

        // Find: elegir mañana y la pista
        composeRule.waitForText(TEST_COURT.name)
        composeRule.onNodeWithText(tomorrow.dayOfMonth.toString()).performClick()
        composeRule.waitForText(TEST_COURT.name)
        composeRule.onNodeWithText(TEST_COURT.name).performClick()

        // Booking: elegir hora y reservar
        composeRule.waitForText("10:00")
        composeRule.onNodeWithText("10:00").performClick()
        composeRule.onNodeWithText(str(R.string.booking_book_now, TEST_COURT.price.toString()))
            .performClick()

        // Sheet: confirmar
        composeRule.waitForText(str(R.string.booking_confirm_title))
        composeRule.onNodeWithText(str(R.string.booking_action_confirm)).performClick()
        composeRule.waitForText(str(R.string.booking_succeeded_title))

        val booking = remote.createdBookings.single()
        assertEquals(TEST_COURT.code, booking.code)
        assertEquals("10:00", booking.hour)
        assertEquals(TEST_USER.uid, booking.userId)
        assertEquals(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant(), booking.date)
    }
}
