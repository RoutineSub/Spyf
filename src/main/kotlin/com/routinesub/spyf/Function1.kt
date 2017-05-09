package com.routinesub.spyf

fun <T,R> spy(function: (T) -> R) = Function1Spy(function, ArgQueue())

fun <T,R> InOrder.spy(function: (T) -> R) = Function1Spy(function, this.argQueue, { this.verificationQueue })

class Function1Spy<T,out R> internal constructor(private val spyed: (T) -> R,
                                                 private val argQueue: ArgQueue,
                                                 private val verificationQueue: () -> VerificationQueue
                                                    = { ImmediateFailVerificationQueue(argQueue) }) {

    private val argKey = object: ArgQueueKey<T>{}

    val function : (T) -> R
        get() = { arg ->
            argQueue.push(argKey, arg)
            spyed(arg)
        }

    val verify : VerificationBuilder1<T>
        get() = VerificationBuilder1({ times, matcher ->
            verificationQueue().push(times, argKey, { matcher.matches(it) }, VerificationError(times, matcher))
        })

}

class VerificationBuilder1<T> internal constructor(private val callback: (Times, Matcher<T>) -> Unit) {

    fun times(times: Int) : ArgVerificationBuilder1<T> = times(atLeast(times))

    fun times(times: Times) = ArgVerificationBuilder1<T>({m -> callback(times, m)})

    fun withArgs(arg: T) = withArgsMatching(eq(arg))

    fun withArgsMatching(arg: Matcher<T>) {
        callback(atLeastOnce, arg)
    }

}

class ArgVerificationBuilder1<T> internal constructor(private val callback: (Matcher<T>) -> Unit){

    fun withArgs(arg: T) = withArgsMatching(eq(arg))

    fun withArgsMatching(arg: Matcher<T>) {
        callback(arg)
    }

}