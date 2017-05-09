package com.routinesub.spyf

import org.junit.Assert
import org.junit.Test

class InOrderTests {


    @Test
    fun inOrderOneFunction() {
        val inOrder = InOrder()
        val spy = inOrder.spy{ _: String -> "Test" }
        spy.function("1")
        spy.function("2")
        inOrder.verify {
            spy.verify.withArgs("1")
            spy.verify.withArgs("2")
        }
    }

    @Test
    fun inOrderOneFunctionFailsOutOfOrder() {
        val inOrder = InOrder()
        val spy = inOrder.spy{ _: String -> "Test" }
        spy.function("1")
        spy.function("2")
        try {
            inOrder.verify {
                spy.verify.withArgs("2")
                spy.verify.withArgs("1")
            }
        } catch (ae: AssertionError) {
            Assert.assertEquals("Expected call matching (1) at least once", ae.message)
            return
        }
        Assert.fail("Expected inOrder.verify to throw an assertion error")
    }

    @Test
    fun inOrderTwoFunctions() {
        val inOrder = InOrder()
        val spy1 = inOrder.spy { _: String -> "Test" }
        val spy2 = inOrder.spy { _: String, _: Int -> 5 }

        //call the functions
        spy1.function("test")
        spy1.function("test")
        spy2.function("test", 3)
        spy1.function("test")

        inOrder.verify {
            spy1.verify.times(2).withArgs("test")
            spy2.verify.times(1).withArgs("test", 3)
            spy1.verify.times(once).withArgs("test")
        }
    }

    @Test
    fun inOrderTwoFunctionsConsumesAll() {
        val inOrder = InOrder()
        val spy1 = inOrder.spy { _: String -> "Test" }
        val spy2 = inOrder.spy { _: String, _: Int -> 5 }
        //call the functions
        spy1.function("test")
        spy1.function("test")
        spy2.function("test", 3)
        spy1.function("test")

        try {
            inOrder.verify {
                spy1.verify.times(3).withArgs("test")
                spy2.verify.times(1).withArgs("test", 3)
            }
        } catch (ae: AssertionError) {
            Assert.assertEquals("Expected call matching (test,3) at least 1 time", ae.message)
            return
        }
        Assert.fail("Expected in order verification to have failed.")
    }

    @Test
    fun inOrderIndependentVerifications() {
        val inOrder = InOrder()
        val spy = inOrder.spy { _: String -> Unit }
        spy.function("test")
        spy.function("test")
        spy.function("1")
        spy.function("test")
        spy.function("2")

        spy.verify.times(once).withArgs("2")
        inOrder.verify {
            spy.verify.times(3).withArgs("test")
            spy.verify.times(once).withArgs("2")
        }
        inOrder.verify {
            spy.verify.times(once).withArgs("1")
            spy.verify.times(once).withArgs("2")
        }
    }

}
