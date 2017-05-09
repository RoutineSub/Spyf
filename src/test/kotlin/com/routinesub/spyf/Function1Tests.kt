package com.routinesub.spyf

import org.junit.Assert
import org.junit.Test

class Function1Tests {

    @Test
    fun spyCapturesArguments() {
        val spy = spy{ _: String -> "Test" }
        //call function twice with 2 different strings
        spy.function("1")
        spy.function("2")
        //verify that both 1 and 2 are captured
        spy.verify.times(once).withArgs("1")
        spy.verify.times(once).withArgs("2")
    }

    @Test
    fun spyThrowsWhenArgsDontMatch() {
        val spy = spy { _: String -> "Test" }
        spy.function("2")
        try {
            spy.verify.times(once).withArgs("1")
        } catch (ae: AssertionError) {
            Assert.assertEquals("Expected call matching (1) once", ae.message)
            return
        }
        Assert.fail("Expected verification to thrown an exception")
    }

    @Test
    fun spyWithoutTimesIsAtLeastOnce() {
        val spy = spy { _: String -> "Test" }
        try {
            spy.verify.withArgs("1")
        } catch (_ : AssertionError) {
            spy.function("1")
            spy.verify.withArgs("1")
            spy.function("1")
            spy.verify.withArgs("1")
            return
        }
        Assert.fail("Expected assertion error to have been thrown when verify was called without calls")
    }

}
