package com.routinesub.spyf

fun <T1,T2,R> spy(function: (T1,T2) -> R) = Function2Spy(function, ArgQueue())

fun <T1,T2,R> InOrder.spy(function: (T1, T2) -> R) = Function2Spy(function, this.argQueue, { this.verificationQueue })

class Function2Spy<T1,T2,out R> internal constructor(private val spyed: (T1,T2) -> R,
                                                     private val argQueue: ArgQueue,
                                                     private val verificationQueue: () -> VerificationQueue
                                                        = { ImmediateFailVerificationQueue(argQueue) }) {

    private data class Arg2<T1,T2>(val arg1: T1, val arg2: T2)

    private val argKey = object: ArgQueueKey<Arg2<T1,T2>>{}

    val function : (T1,T2) -> R
        get() = { arg1, arg2 ->
            argQueue.push(argKey, Arg2(arg1, arg2))
            spyed(arg1, arg2)
        }

    val verify: VerificationBuilder2<T1, T2>
        get() = VerificationBuilder2({times, matcher1, matcher2 ->
            verificationQueue().push(times, argKey, { matcher1.matches(it.arg1) && matcher2.matches(it.arg2) },
                    VerificationError(times, matcher1, matcher2))
        })
}

class VerificationBuilder2<T1,T2> internal
                                    constructor(private val callback: (Times, Matcher<T1>, Matcher<T2>) -> Unit) {

    fun times(times: Int) : ArgVerificationBuilder2<T1,T2> = times(atLeast(times))

    fun times(times: Times) = ArgVerificationBuilder2<T1,T2>({m1, m2 -> callback(times, m1, m2)})

    fun withArgs(arg1: T1, arg2: T2) = withArgsMatching(eq(arg1), eq(arg2))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>) {
        callback(atLeastOnce, arg1, arg2)
    }

}

class ArgVerificationBuilder2<T1,T2> internal constructor(private val callback: (Matcher<T1>, Matcher<T2>) -> Unit){

    fun withArgs(arg1: T1, arg2: T2) = withArgsMatching(eq(arg1), eq(arg2))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>) {
        callback(arg1, arg2)
    }

}