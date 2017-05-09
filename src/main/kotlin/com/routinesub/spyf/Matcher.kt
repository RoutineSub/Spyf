package com.routinesub.spyf


interface Matcher<in T> {

    val describe: String

    fun matches(value: T) : Boolean

}

object `is` {

    infix fun <T> not(matcher: Matcher<T>) : Matcher<T> = object: Matcher<T> {
        override val describe: String
            get() = "is not ${matcher.describe}"

        override fun matches(value: T): Boolean = !matcher.matches(value)
    }

    infix fun <T> not(value: T) : Matcher<T> = not(eq(value))

}

fun <T> eq(value: T) : Matcher<T> = object: Matcher<T> {
    override val describe: String
        get() = when(value) {
            is String -> "\"$value\""
            else -> value.toString()
        }

    override fun matches(v: T): Boolean = value == v
}

fun <T> any() : Matcher<T> = object: Matcher<T> {
    override val describe: String
        get() = "*"

    override fun matches(value: T): Boolean = true
}

fun <T : Comparable<T>> inRange(range: ClosedRange<T>) = object: Matcher<T> {
    override val describe: String
        get() = "(${range.start} .. ${range.endInclusive})"

    override fun matches(value: T): Boolean = value in range
}

fun <T: Any> `null`() = object: Matcher<T?> {
    override val describe: String
        get() = "null"

    override fun matches(value: T?): Boolean = value == null
}

fun <T: CharSequence> emptyString() = object: Matcher<T> {
    override val describe: String
        get() =  "\"\""

    override fun matches(value: T): Boolean = value.isEmpty()
}

fun <T : Collection<*>> empty() =  object: Matcher<T> {
    override val describe: String
        get() = "[]"

    override fun matches(value: T): Boolean = value.isEmpty()
}

fun <T: Collection<*>> size(matcher: Matcher<Int>) = object: Matcher<T> {
    override val describe: String
        get() =  "size ${matcher.describe}"

    override fun matches(value: T): Boolean = matcher.matches(value.size)
}

fun <T: CharSequence> length(matcher: Matcher<Int>) = object: Matcher<T> {
    override val describe: String
        get() = "length ${matcher.describe}"

    override fun matches(value: T): Boolean = matcher.matches(value.length)

}