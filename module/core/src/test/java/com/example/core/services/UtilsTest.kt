package com.example.core.services

import com.example.core.util.isBiggerThan
import com.example.core.util.isEqualTo
import com.example.core.util.isSmallerThan
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class UtilsTest {
    private val threshold = 0.005
    private val thresholdLower = threshold - 0.001
    private val thresholdUpper = threshold + 0.001

    @Test
    fun testIsEqualTo_WithinThreshold() {
        assertTrue(0.01.isEqualTo(0.01 + thresholdLower))
        assertTrue((0.01 + thresholdLower).isEqualTo(0.01))
    }

    @Test
    fun testIsEqualTo_OutsideThreshold() {
        assertFalse(0.01.isEqualTo(0.01 + thresholdUpper))
        assertFalse((0.01 + thresholdUpper).isEqualTo(0.01))
        assertFalse(0.01.isEqualTo(0.01 - thresholdUpper))
        assertFalse((0.01 - thresholdUpper).isEqualTo(0.01))
    }

    @Test
    fun testIsSmallerThan_True() {
        assertTrue(0.01.isSmallerThan(0.02))
        assertTrue((0.001 + thresholdLower).isSmallerThan(0.02))
    }

    @Test
    fun testIsSmallerThan_FalseEqual() {
        assertFalse((0.01 + threshold).isSmallerThan(0.01))
        assertFalse(0.01.isSmallerThan((0.01 + thresholdLower)))
    }

    @Test
    fun testIsSmallerThan_FalseGreater() {
        assertFalse((0.01 + threshold).isSmallerThan(0.01))
    }

    @Test
    fun testIsBiggerThan_True() {
        assertTrue(0.02.isBiggerThan(0.01))
        assertTrue(0.02.isBiggerThan((0.001 + thresholdLower)))
        assertTrue((0.01 + thresholdUpper).isBiggerThan(0.01))
    }

    @Test
    fun testIsBiggerThan_FalseEqual() {
        assertFalse((0.01 + thresholdLower).isBiggerThan(0.01))
    }

    @Test
    fun testIsBiggerThan_FalseSmaller() {
        assertFalse((0.01 + threshold).isBiggerThan(0.02))
    }
}