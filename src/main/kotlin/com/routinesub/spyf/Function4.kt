package com.routinesub.spyf

fun <T1,T2,T3,T4,R> spy(function: (T1,T2,T3,T4) -> R) = Function4Spy(function, ArgQueue())

fun <T1,T2,T3,T4,R> InOrder.spy(function: (T1,T2,T3,T4) -> R) = Function4Spy(function, this.argQueue, { this.verificationQueue })

class Function4Spy<T1,T2,T3,T4,out R> internal constructor(private val spyed: (T1,T2,T3,T4) -> R,
                                                         private val argQueue: ArgQueue,
                                                         private val verificationQueue: () -> VerificationQueue
                                                         = { ImmediateFailVerificationQueue(argQueue) }) {

    private data class Arg4<T1,T2,T3,T4>(val arg1: T1, val arg2: T2, val arg3: T3, val arg4: T4)

    private val argKey = object: ArgQueueKey<Arg4<T1,T2,T3,T4>>{}

    val function : (T1,T2,T3,T4) -> R
        get() = { arg1, arg2, arg3, arg4 ->
            argQueue.push(argKey, Arg4(arg1, arg2, arg3, arg4))
            spyed(arg1, arg2, arg3, arg4)
        }

    val verify: VerificationBuilder4<T1,T2,T3,T4>
        get() = VerificationBuilder4({times, matcher1, matcher2, matcher3, matcher4 ->
            verificationQueue().push(times, argKey,
                    { matcher1.matches(it.arg1) && matcher2.matches(it.arg2) && matcher3.matches(it.arg3) &&
                        matcher4.matches(it.arg4)},
                    VerificationError(times, matcher1, matcher2, matcher3, matcher4))
        })
}

class VerificationBuilder4<T1,T2,T3,T4> internal
constructor(private val callback: (Times, Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>) -> Unit) {

    fun times(times: Int) : ArgVerificationBuilder4<T1,T2,T3,T4> = times(atLeast(times))

    fun times(times: Times) = ArgVerificationBuilder4<T1,T2,T3,T4>({m1, m2, m3, m4 -> callback(times, m1, m2, m3, m4)})

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4) = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>) {
        callback(atLeastOnce, arg1, arg2, arg3, arg4)
    }

}

class ArgVerificationBuilder4<T1,T2,T3,T4> internal constructor(
        private val callback: (Matcher<T1>, Matcher<T2>, Matcher<T3>, Matcher<T4>) -> Unit){

    fun withArgs(arg1: T1, arg2: T2, arg3: T3, arg4: T4) = withArgsMatching(eq(arg1), eq(arg2), eq(arg3), eq(arg4))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>, arg4: Matcher<T4>) {
        callback(arg1, arg2, arg3, arg4)
    }

}
