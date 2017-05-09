package com.routinesub.spyf

fun <T1,T2,T3,R> spy(function: (T1,T2, T3) -> R) = Function3Spy(function, ArgQueue())

fun <T1,T2,T3,R> InOrder.spy(function: (T1, T2, T3) -> R) = Function3Spy(function, this.argQueue, { this.verificationQueue })

class Function3Spy<T1,T2, T3,out R> internal constructor(private val spyed: (T1,T2,T3) -> R,
                                                     private val argQueue: ArgQueue,
                                                     private val verificationQueue: () -> VerificationQueue
                                                     = { ImmediateFailVerificationQueue(argQueue) }) {

    private data class Arg3<T1,T2,T3>(val arg1: T1, val arg2: T2, val arg3: T3)

    private val argKey = object: ArgQueueKey<Arg3<T1,T2,T3>>{}

    val function : (T1,T2,T3) -> R
        get() = { arg1, arg2, arg3 ->
            argQueue.push(argKey, Arg3(arg1, arg2, arg3))
            spyed(arg1, arg2, arg3)
        }

    val verify: VerificationBuilder3<T1, T2, T3>
        get() = VerificationBuilder3({times, matcher1, matcher2, matcher3 ->
            verificationQueue().push(times, argKey,
                    { matcher1.matches(it.arg1) && matcher2.matches(it.arg2) && matcher3.matches(it.arg3) },
                    VerificationError(times, matcher1, matcher2, matcher3))
        })
}

class VerificationBuilder3<T1,T2, T3> internal
constructor(private val callback: (Times, Matcher<T1>, Matcher<T2>, Matcher<T3>) -> Unit) {

    fun times(times: Int) : ArgVerificationBuilder3<T1,T2, T3> = times(atLeast(times))

    fun times(times: Times) = ArgVerificationBuilder3<T1,T2, T3>({m1, m2, m3 -> callback(times, m1, m2, m3)})

    fun withArgs(arg1: T1, arg2: T2, arg3: T3) = withArgsMatching(eq(arg1), eq(arg2), eq(arg3))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>) {
        callback(atLeastOnce, arg1, arg2, arg3)
    }

}

class ArgVerificationBuilder3<T1,T2, T3> internal constructor(
        private val callback: (Matcher<T1>, Matcher<T2>, Matcher<T3>) -> Unit){

    fun withArgs(arg1: T1, arg2: T2, arg3: T3) = withArgsMatching(eq(arg1), eq(arg2), eq(arg3))

    fun withArgsMatching(arg1: Matcher<T1>, arg2: Matcher<T2>, arg3: Matcher<T3>) {
        callback(arg1, arg2, arg3)
    }

}
