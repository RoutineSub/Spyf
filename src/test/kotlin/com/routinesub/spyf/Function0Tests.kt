package com.routinesub.spyf

import org.junit.Assert
import org.junit.Test

class Function0Tests {


    @Test
    fun testSpyingWithSuccessfulValidation() {
        val spy = spy { -> "Test" }
        //call the function twice
        spy.function()
        spy.function()
        //verify that the function was called twice
        spy.verify.times(exactly(2))
    }

    @Test
    fun testSpyingWithFailedValidation() {
        val spy = spy { -> "Test" }
        //call the function twice
        spy.function()
        spy.function()
        try {
            spy.verify.times(exactly(3))
        } catch (ae: AssertionError) {
            Assert.assertEquals("Expected call matching () exactly 3 times", ae.message)
            return
        }
        Assert.fail("Expected assertion error to be thrown")
    }

    @Test
    fun testSpyingPassesThroughCalls() {
        var counter = 0
        val spy = spy { -> counter++ }
        spy.function()
        spy.function()
        assert(counter == 2, {"Expected counter to be incremented twice"})
    }

}


