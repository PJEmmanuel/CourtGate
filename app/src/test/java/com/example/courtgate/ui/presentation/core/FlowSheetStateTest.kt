package com.example.courtgate.ui.presentation.core

import com.example.courtgate.domain.models.DomainError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FlowSheetStateTest {

    private val target = "10:00"

    @Test
    fun `targetOrNull returns the target in every visible state`() {
        assertNull(FlowSheetState.Hidden.targetOrNull)
        assertEquals(target, FlowSheetState.Confirming(target).targetOrNull)
        assertEquals(target, FlowSheetState.Submitting(target).targetOrNull)
        assertEquals(target, FlowSheetState.Failed(target, DomainError.Remote.ServerError).targetOrNull)
        assertEquals(target, FlowSheetState.Succeeded(target).targetOrNull)
    }

    @Test
    fun `sheet can be dismissed except while submitting or after success`() {
        assertTrue(FlowSheetState.Hidden.canDismiss)
        assertTrue(FlowSheetState.Confirming(target).canDismiss)
        assertTrue(FlowSheetState.Failed(target, DomainError.Remote.ServerError).canDismiss)
        assertFalse(FlowSheetState.Submitting(target).canDismiss)
        assertFalse(FlowSheetState.Succeeded(target).canDismiss)
    }
}
