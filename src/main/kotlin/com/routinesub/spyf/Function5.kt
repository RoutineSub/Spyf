package com.routinesub.spyf

fun <T1,T2,T3,T4,T5,R> spy(function: (T1,T2,T3,T4,T5) -> R) = Function5Spy(function, ArgQueue())

fun <T1,T2,T3,T4,T5,R> InOrder.spy(function: (T1,T2,T3,T4,T5) -> R)
        = Function5Spy(function, this.argQueue, { this.verificationQueue })

class Function5Spy<T1,T2,T3,T4,T5,out R> internal constructor(private val spyed: (T1,T2,T3,T4,T5) -> R,
                                                           private val argQueue: ArgQueue,
                                                           private val verificationQueue: () -> VerificationQueue
                                                           = { ImmediateFailVerificationQueue(argQueue) }) {

    private data class Arg5<T1,T2,T3,T4,T5>(val arg1: T1, val arg2: T2, val arg3: T3, val arg4: T4, val arg5: T5)

    private val argKey = object: ArgQueueKey<Arg5<T1,T2,T3,T4,T5>>{}

    val function : (T1,T2,T3,T4,T5) -> R
        get() = { arg1, arg2, arg3, arg4, arg5 ->
            argQueue.push(argKey, Arg5(arg1, arg2, arg3, arg4, arg5))
            spyed(arg1, arg2, arg3, arg4, arg5)
        }

    val verify: VerificationBuilder5<T1,T2,T3,T4,T5>
        get() = VerificationBuilder5({times, matcher1, matcher2, matcher3, matcher4, matcher5 ->
            verificationQueue().push(times, argKey,
                    { matcher1.matches(it.arg1) && matcher2.matches(it.arg2) && matcher3.matches(it.arg3) &&
                            matcher4.matches(it.arg4) && matcher5.matches(it.arg5)},
                    VerificationError(times, matcher1, matcher2, matcher3, matcher4, matcher5))
        })
}

class VerificationBuilder5<T1,T2,T3,T4,T5> internal
constructor(private val callback: (Times, Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>, Matcher<T5>) -> Unit) {

    fun times(times: Int) : ArgVerificationBuilder5<T1,T2,T3,T4,T5> = times(atLeast(times))

    fun times(times: Times) = ArgVerificationBuilder5<T1,T2,T3,T4,T5>({m1, m2, m3, m4, m5 ->
        callback(times, m1, m2, m3, m4, m5)
    })

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5)
            = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4), eq(arg5))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>, arg5: Matcher<T5>) {
        callback(atLeastOnce, arg1, arg2, arg3, arg4, arg5)
    }

}

class ArgVerificationBuilder5<T1,T2,T3,T4,T5> internal constructor(
        private val callback: (Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>, Matcher<T5>) -> Unit){

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5)
            = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4), eq(arg5))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>, arg5: Matcher<T5>) {
        callback(arg1, arg2, arg3, arg4, arg5)
    }

}
