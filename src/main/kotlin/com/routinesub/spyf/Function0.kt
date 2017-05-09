package com.routinesub.spyf

fun <R> spy(function: () -> R) = Function0Spy(function, ArgQueue())

fun <R> InOrder.spy(function: () -> R) = Function0Spy(function, this.argQueue, { this.verificationQueue })

class Function0Spy<out R> internal constructor (private val spyed: () -> R,
                                                private val args: ArgQueue,
                                                private val verificationQueue: () -> VerificationQueue
                                                    = { ImmediateFailVerificationQueue(args)}) {

    private val argKey = object: ArgQueueKey<Unit>{}

    val function: () -> R
        get() = {
            args.push(argKey, Unit)
            spyed()
        }

    val verify : VerificationBuilder0
        get() = VerificationBuilder0({ times ->
            verificationQueue().push(times, argKey, { true }, VerificationError(times))
        })

}

class VerificationBuilder0 internal constructor(private val callback: (Times) -> Unit) {

    fun times(times: Int) = times(atLeast(times))

    fun times(times: Times) {
        callback(times)
    }

}
