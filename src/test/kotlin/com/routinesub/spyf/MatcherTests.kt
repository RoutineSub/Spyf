package com.routinesub.spyf

import org.junit.Assert
import org.junit.Test

class MatcherTests {

    @Test
    fun testEq() {
        val matcher = eq("Test")
        Assert.assertEquals("\"Test\"", matcher.describe)
        Assert.assertTrue("Expected to match \"Test\"", matcher.matches("Test"))
        Assert.assertFalse("Expected to not match \"Fail\"", matcher.matches("Fail"))
    }

    @Test
    fun testIsNot() {
        val matcher = `is` not eq("Test")
        Assert.assertEquals("is not \"Test\"", matcher.describe)
        Assert.assertFalse("expected not to match \"Test\"", matcher.matches("Test"))
        Assert.assertTrue("expected to match \"Fail\"", matcher.matches("Fail"))
    }

    @Test
    fun testNull() {
        val matcher = `null`<String>()
        Assert.assertEquals("null", matcher.describe)
        Assert.assertTrue("expected to match null", matcher.matches(null))
        Assert.assertFalse("expecte to not match a value", matcher.matches("Fail"))
    }

    @Test
    fun testRange() {
        val matcher = inRange(0 .. 4)
        Assert.assertEquals("(0 .. 4)", matcher.describe)
        Assert.assertTrue("expected to match 2", matcher.matches(2))
        Assert.assertFalse("expected not to match 5", matcher.matches(5))
    }

    @Test
    fun testLongRange() {
        val matcher = inRange(0L .. 4L)
        Assert.assertEquals("(0 .. 4)", matcher.describe)
        Assert.assertTrue("expected to match 2", matcher.matches(2))
        Assert.assertFalse("expected not to match 5", matcher.matches(5))
    }

    @Test
    fun testEmptyString() {
        val matcher = emptyString<String>()
        Assert.assertEquals("\"\"", matcher.describe)
        Assert.assertTrue("expected to match empty string", matcher.matches(""))
        Assert.assertFalse("expected to not match string with value", matcher.matches("Fail"))
    }

    @Test
    fun testEmptyList() {
        val matcher = empty<List<String>>()
        Assert.assertEquals("[]", matcher.describe)
        Assert.assertTrue("expected to match empty list", matcher.matches(listOf()))
        Assert.assertFalse("expected to not match list with value", matcher.matches(listOf("Test")))
    }

    @Test
    fun testSize() {
        val matcher = size<List<String>>(inRange(1..3))
        Assert.assertEquals("size (1 .. 3)", matcher.describe)
        Assert.assertTrue("expected to match list of length 2", matcher.matches(listOf("1", "2")))
        Assert.assertFalse("expected to not match list of length 4", matcher.matches(listOf("1", "2", "3", "4")))
    }

}