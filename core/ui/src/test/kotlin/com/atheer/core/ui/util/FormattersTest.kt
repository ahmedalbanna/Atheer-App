package com.atheer.core.ui.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun `formats zero`() {
        assertEquals("0", formatViewsCount(0))
    }

    @Test
    fun `formats small counts as-is`() {
        assertEquals("999", formatViewsCount(999))
    }

    @Test
    fun `formats thousands`() {
        assertEquals("125 ألف", formatViewsCount(125_000))
    }

    @Test
    fun `formats millions with one decimal`() {
        assertEquals("1.2 مليون", formatViewsCount(1_240_000))
    }

    @Test
    fun `drops trailing zero for whole millions`() {
        assertEquals("2 مليون", formatViewsCount(2_000_000))
    }

    @Test
    fun `handles large millions without truncation bug`() {
        // Old implementation produced "12." via take(3)
        assertEquals("12.5 مليون", formatViewsCount(12_500_000))
    }
}
