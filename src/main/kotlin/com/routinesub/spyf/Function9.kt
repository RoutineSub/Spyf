package com.routinesub.spyf


fun <T1,T2,T3,T4,T5,T6,T7,T8,T9,R> spy(function: (T1,T2,T3,T4,T5,T6,T7,T8,T9) -> R) = Function9Spy(function, ArgQueue())

fun <T1,T2,T3,T4,T5,T6,T7,T8,T9,R> InOrder.spy(function: (T1,T2,T3,T4,T5,T6,T7,T8,T9) -> R)
        = Function9Spy(function, this.argQueue, { this.verificationQueue })

class Function9Spy<T1,T2,T3,T4,T5,T6,T7,T8,T9,out R> internal constructor(private val spyed: (T1,T2,T3,T4,T5,T6,T7,T8,T9) -> R,
                                                                       private val argQueue: ArgQueue,
                                                                       private val verificationQueue: () -> VerificationQueue
                                                                       = { ImmediateFailVerificationQueue(argQueue) }) {

    private data class Arg9<T1,T2,T3,T4,T5,T6,T7,T8,T9>(val arg1: T1, val arg2: T2, val arg3: T3, val arg4: T4,
                                                     val arg5: T5, val arg6: T6, val arg7: T7, val arg8: T8,
                                                        val arg9: T9)

    private val argKey = object: ArgQueueKey<Arg9<T1,T2,T3,T4,T5,T6,T7,T8,T9>>{}

    val function : (T1,T2,T3,T4,T5,T6,T7,T8,T9) -> R
        get() = { arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9 ->
            argQueue.push(argKey, Arg9(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9))
            spyed(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
        }

    val verify: VerificationBuilder9<T1,T2,T3,T4,T5,T6,T7,T8,T9>
        get() = VerificationBuilder9({times, matcher1, matcher2, matcher3, matcher4, matcher5, matcher6, matcher7,
                                      matcher8, matcher9 ->
            verificationQueue().push(times, argKey,
                    { matcher1.matches(it.arg1) && matcher2.matches(it.arg2) && matcher3.matches(it.arg3) &&
                            matcher4.matches(it.arg4) && matcher5.matches(it.arg5) && matcher6.matches(it.arg6) &&
                            matcher7.matches(it.arg7) && matcher8.matches(it.arg8) && matcher9.matches(it.arg9) },
                    VerificationError(times, matcher1, matcher2, matcher3, matcher4, matcher5, matcher6, matcher7,
                            matcher8, matcher9))
        })
}

class VerificationBuilder9<T1,T2,T3,T4,T5,T6,T7,T8,T9> internal constructor(
        private val callback: (Times, Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>, Matcher<T5>,
                               Matcher<T6>, Matcher<T7>, Matcher<T8>, Matcher<T9>) -> Unit) {

    fun times(times: Int) : ArgVerificationBuilder9<T1,T2,T3,T4,T5,T6,T7,T8,T9> = times(atLeast(times))

    fun times(times: Times) = ArgVerificationBuilder9<T1,T2,T3,T4,T5,T6,T7,T8,T9>({m1, m2, m3, m4, m5, m6, m7, m8, m9 ->
        callback(times, m1, m2, m3, m4, m5, m6, m7, m8, m9)
    })

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8, arg9: T9)
            = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4), eq(arg5), eq(arg6), eq(arg7), eq(arg8), eq(arg9))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>,
                         arg5: Matcher<T5>, arg6: Matcher<T6>, arg7: Matcher<T7>, arg8: Matcher<T8>,
                         arg9: Matcher<T9>) {
        callback(atLeastOnce, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    }

}

class ArgVerificationBuilder9<T1,T2,T3,T4,T5,T6,T7,T8,T9> internal constructor(
        private val callback: (Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>, Matcher<T5>, Matcher<T6>,
                               Matcher<T7>, Matcher<T8>, Matcher<T9>) -> Unit){

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5, arg6: T6, arg7: T7, arg8: T8, arg9: T9)
            = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4), eq(arg5), eq(arg6), eq(arg7), eq(arg8), eq(arg9))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>,
                         arg5: Matcher<T5>, arg6: Matcher<T6>, arg7: Matcher<T7>, arg8: Matcher<T8>,
                         arg9: Matcher<T9>) {
        callback(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    }

}