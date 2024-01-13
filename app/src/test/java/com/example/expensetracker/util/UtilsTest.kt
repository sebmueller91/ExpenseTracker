package com.example.expensetracker.util

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class UtilsTest {
    private val threshold = 0.005
    private val threshold_l = threshold - 0.001
    private val threshold_u = threshold + 0.001

    @Test
    fun testIsEqualTo_WithinThreshold() {
        assertTrue(0.01.isEqualTo(0.01 + threshold_l))
        assertTrue((0.01 + threshold_l).isEqualTo(0.01))
    }

    @Test
    fun testIsEqualTo_OutsideThreshold() {
        assertFalse(0.01.isEqualTo(0.01 + threshold_u))
        assertFalse((0.01 + threshold_u).isEqualTo(0.01))
        assertFalse(0.01.isEqualTo(0.01 - threshold_u))
        assertFalse((0.01 - threshold_u).isEqualTo(0.01))
    }

    @Test
    fun testIsSmallerThan_True() {
        assertTrue(0.01.isSmallerThan(0.02))
        assertTrue((0.001 + threshold_l).isSmallerThan(0.02))
    }

    @Test
    fun testIsSmallerThan_FalseEqual() {
        assertFalse((0.01 + threshold).isSmallerThan(0.01))
        assertFalse(0.01.isSmallerThan((0.01 + threshold_l)))
    }

    @Test
    fun testIsSmallerThan_FalseGreater() {
        assertFalse((0.01 + threshold).isSmallerThan(0.01))
    }

    @Test
    fun testIsBiggerThan_True() {
        assertTrue(0.02.isBiggerThan(0.01))
        assertTrue(0.02.isBiggerThan((0.001 + threshold_l)))
        assertTrue((0.01 + threshold_u).isBiggerThan(0.01))
    }

    @Test
    fun testIsBiggerThan_FalseEqual() {
        assertFalse((0.01 + threshold_l).isBiggerThan(0.01))
    }

    @Test
    fun testIsBiggerThan_FalseSmaller() {
        assertFalse((0.01 + threshold).isBiggerThan(0.02))
    }
}