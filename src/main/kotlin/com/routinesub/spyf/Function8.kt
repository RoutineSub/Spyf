package com.routinesub.spyf

fun <T1,T2,T3,T4,T5,T6,T7,T8,R> spy(function: (T1,T2,T3,T4,T5,T6,T7,T8) -> R) = Function8Spy(function, ArgQueue())

fun <T1,T2,T3,T4,T5,T6,T7,T8,R> InOrder.spy(function: (T1,T2,T3,T4,T5,T6,T7,T8) -> R)
        = Function8Spy(function, this.argQueue, { this.verificationQueue })

class Function8Spy<T1,T2,T3,T4,T5,T6,T7,T8,out R> internal constructor(private val spyed: (T1,T2,T3,T4,T5,T6,T7,T8) -> R,
                                                                    private val argQueue: ArgQueue,
                                                                    private val verificationQueue: () -> VerificationQueue
                                                                    = { ImmediateFailVerificationQueue(argQueue) }) {

    private data class Arg8<T1,T2,T3,T4,T5,T6,T7,T8>(val arg1: T1, val arg2: T2, val arg3: T3, val arg4: T4,
                                                  val arg5: T5, val arg6: T6, val arg7: T7, val arg8: T8)

    private val argKey = object: ArgQueueKey<Arg8<T1,T2,T3,T4,T5,T6,T7,T8>>{}

    val function : (T1,T2,T3,T4,T5,T6,T7,T8) -> R
        get() = { arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8 ->
            argQueue.push(argKey, Arg8(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8))
            spyed(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
        }

    val verify: VerificationBuilder8<T1,T2,T3,T4,T5,T6,T7,T8>
        get() = VerificationBuilder8({times, matcher1, matcher2, matcher3, matcher4, matcher5, matcher6, matcher7,
                                        matcher8 ->
            verificationQueue().push(times, argKey,
                    { matcher1.matches(it.arg1) && matcher2.matches(it.arg2) && matcher3.matches(it.arg3) &&
                            matcher4.matches(it.arg4) && matcher5.matches(it.arg5) && matcher6.matches(it.arg6) &&
                            matcher7.matches(it.arg7) && matcher8.matches(it.arg8) },
                    VerificationError(times, matcher1, matcher2, matcher3, matcher4, matcher5, matcher6, matcher7,
                                        matcher8))
        })
}

class VerificationBuilder8<T1,T2,T3,T4,T5,T6,T7,T8> internal constructor(
        private val callback: (Times, Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>, Matcher<T5>,
                               Matcher<T6>, Matcher<T7>, Matcher<T8>) -> Unit) {

    fun times(times: Int) : ArgVerificationBuilder8<T1,T2,T3,T4,T5,T6,T7,T8> = times(atLeast(times))

    fun times(times: Times) = ArgVerificationBuilder8<T1,T2,T3,T4,T5,T6,T7,T8>({m1, m2, m3, m4, m5, m6, m7, m8 ->
        callback(times, m1, m2, m3, m4, m5, m6, m7, m8)
    })

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8)
            = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4), eq(arg5), eq(arg6), eq(arg7), eq(arg8))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>,
                         arg5: Matcher<T5>, arg6: Matcher<T6>, arg7: Matcher<T7>, arg8: Matcher<T8>) {
        callback(atLeastOnce, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
    }

}

class ArgVerificationBuilder8<T1,T2,T3,T4,T5,T6,T7,T8> internal constructor(
        private val callback: (Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>, Matcher<T5>, Matcher<T6>,
                               Matcher<T7>, Matcher<T8>) -> Unit){

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8)
            = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4), eq(arg5), eq(arg6), eq(arg7), eq(arg8))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>,
                         arg5: Matcher<T5>, arg6: Matcher<T6>, arg7: Matcher<T7>, arg8: Matcher<T8>) {
        callback(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)
    }

}
