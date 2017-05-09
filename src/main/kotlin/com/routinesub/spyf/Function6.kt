package com.routinesub.spyf

fun <T1,T2,T3,T4,T5,T6,R> spy(function: (T1,T2,T3,T4,T5,T6) -> R) = Function6Spy(function, ArgQueue())

fun <T1,T2,T3,T4,T5,T6,R> InOrder.spy(function: (T1,T2,T3,T4,T5,T6) -> R)
        = Function6Spy(function, this.argQueue, { this.verificationQueue })

class Function6Spy<T1,T2,T3,T4,T5,T6,out R> internal constructor(private val spyed: (T1,T2,T3,T4,T5,T6) -> R,
                                                              private val argQueue: ArgQueue,
                                                              private val verificationQueue: () -> VerificationQueue
                                                              = { ImmediateFailVerificationQueue(argQueue) }) {

    private data class Arg6<T1,T2,T3,T4,T5,T6>(val arg1: T1, val arg2: T2, val arg3: T3, val arg4: T4,
                                               val arg5: T5, val arg6: T6)

    private val argKey = object: ArgQueueKey<Arg6<T1,T2,T3,T4,T5,T6>>{}

    val function : (T1,T2,T3,T4,T5,T6) -> R
        get() = { arg1, arg2, arg3, arg4, arg5, arg6 ->
            argQueue.push(argKey, Arg6(arg1, arg2, arg3, arg4, arg5, arg6))
            spyed(arg1, arg2, arg3, arg4, arg5, arg6)
        }

    val verify: VerificationBuilder6<T1,T2,T3,T4,T5,T6>
        get() = VerificationBuilder6({times, matcher1, matcher2, matcher3, matcher4, matcher5, matcher6 ->
            verificationQueue().push(times, argKey,
                    { matcher1.matches(it.arg1) && matcher2.matches(it.arg2) && matcher3.matches(it.arg3) &&
                            matcher4.matches(it.arg4) && matcher5.matches(it.arg5) && matcher6.matches(it.arg6)},
                    VerificationError(times, matcher1, matcher2, matcher3, matcher4, matcher5, matcher6))
        })
}

class VerificationBuilder6<T1,T2,T3,T4,T5,T6> internal constructor(
        private val callback: (Times, Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>, Matcher<T5>,
                               Matcher<T6>) -> Unit) {

    fun times(times: Int) : ArgVerificationBuilder6<T1,T2,T3,T4,T5,T6> = times(atLeast(times))

    fun times(times: Times) = ArgVerificationBuilder6<T1,T2,T3,T4,T5,T6>({m1, m2, m3, m4, m5, m6 ->
        callback(times, m1, m2, m3, m4, m5, m6)
    })

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6)
            = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4), eq(arg5), eq(arg6))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>,
                         arg5: Matcher<T5>, arg6: Matcher<T6>) {
        callback(atLeastOnce, arg1, arg2, arg3, arg4, arg5, arg6)
    }

}

class ArgVerificationBuilder6<T1,T2,T3,T4,T5,T6> internal constructor(
        private val callback: (Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>, Matcher<T5>, Matcher<T6>) -> Unit){

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6)
            = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4), eq(arg5), eq(arg6))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>,
                         arg5: Matcher<T5>, arg6: Matcher<T6>) {
        callback(arg1, arg2, arg3, arg4, arg5, arg6)
    }

}
