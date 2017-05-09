package com.routinesub.spyf


interface Times {

    fun matches(value: Int) : Boolean
}

object once : Times {

    override fun matches(value: Int): Boolean = value == 1

    override fun toString(): String = "once"
}

object never : Times {

    override fun matches(value: Int): Boolean = value == 0

    override fun toString(): String = "never"
}

object atLeastOnce : Times {

    override fun matches(value: Int): Boolean = value >= 1

    override fun toString(): String = "at least once"
}

object atMostOnce : Times {

    override fun matches(value: Int): Boolean = value <= 1

    override fun toString(): String = "at most once"
}

fun exactly(times: Int) : Times = object: Times {

    override fun matches(value: Int): Boolean = value == times

    override fun toString(): String = "exactly $times time${if(times > 1) "s" else ""}"
}

fun atLeast(times: Int) : Times = object: Times {

    override fun matches(value: Int): Boolean = value >= times

    override fun toString(): String = "at least $times time${if(times > 1) "s" else ""}"
}

fun atMost(times: Int) : Times = object: Times {

    override fun matches(value: Int): Boolean = value <= times

    override fun toString(): String = "at most $times time${if(times > 1) "s" else ""}"
}

interface TimesMatcher : Times, Matcher<Int>

fun inRange(range: IntRange) : TimesMatcher = object: TimesMatcher {
    override val describe: String
        get() = "(${range.start} .. ${range.endInclusive})"

    override fun matches(value: Int): Boolean = value in range

    override fun toString(): String = "between ${range.first} and ${range.last} times"
}
