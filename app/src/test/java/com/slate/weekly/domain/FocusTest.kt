package com.slate.weekly.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusTest {

    @Test
    fun `focus end stays inside a day`() {
        val now = 1_000_000L
        assertEquals(now + 3_600_000L, focusEnd(now, 1))
        assertEquals(now + 24 * 3_600_000L, focusEnd(now, 24))
        assertEquals(now + 3_600_000L, focusEnd(now, 0))
        assertEquals(now + 24 * 3_600_000L, focusEnd(now, 99))
    }

    @Test
    fun `focus is active only before its end`() {
        assertTrue(isFocusActive(2_000L, 1_000L))
        assertFalse(isFocusActive(1_000L, 1_000L))
        assertFalse(isFocusActive(999L, 1_000L))
    }
}
