package com.routinesub.spyf

internal interface ArgQueueKey<in T>

internal class ArgQueue {

    private val args = mutableListOf<Pair<ArgQueueKey<*>, *>>()
    private val lock = Any()

    fun <T> push(key: ArgQueueKey<T>, value: T) {
        synchronized(lock) {
            args.add(key to value)
        }
    }

    fun <T> get(key: ArgQueueKey<T>) : List<T> {
        return args.filter { it.first == key }.map { it.second as T }
    }

    private data class VerifyKey<in T>(val times: Times, val key: ArgQueueKey<T>,
                                 val predicate: (T) -> Boolean, val error: VerificationError )

    fun getVerificationQueue() : VerificationQueue {
        val frozenArgs = mutableListOf<Pair<ArgQueueKey<*>, *>>()
        frozenArgs.addAll(args)
        return object: VerificationQueue {

            private val verifications = mutableListOf<VerifyKey<*>>()
            private val lock = Any()

            override fun <T> push(times: Times, key: ArgQueueKey<T>, predicate: (T) -> Boolean, error: VerificationError) {
                synchronized(lock) {
                    verifications.add(VerifyKey(times, key, predicate, error))
                }
            }

            override fun verify() {
                //for each verification
                verifications.forEach {
                    //skip the args until the count starts matching
                    var count = 0
                    while(!it.times.matches(count)) {
                        //haven't matched yet and we're out of things to match
                        if (frozenArgs.isEmpty()) {
                            throw it.error
                        }
                        //take the first item
                        val item = frozenArgs.removeAt(0)
                        //matches the key, and the predicate matches
                        if (item.first == it.key && (it.predicate as (Any?) -> Boolean)(item.second)) {
                            //increase count
                            count++
                        }
                    }
                    //if we make it out of the while loop without throwing continue to the next verification
                }
            }
        }
    }

}

internal interface VerificationQueue {
    fun <T> push(times: Times, key: ArgQueueKey<T>, predicate: (T) -> Boolean, error: VerificationError)
    fun verify() : Unit
}

internal class ImmediateFailVerificationQueue(private val argQueue: ArgQueue) : VerificationQueue {

    override fun <T> push(times: Times, key: ArgQueueKey<T>, predicate: (T) -> Boolean, error: VerificationError) {
        val queue = argQueue.getVerificationQueue()
        queue.push(times, key, predicate, error)
        queue.verify()
    }

    override fun verify() {}

}

class InOrder {

    internal val argQueue = ArgQueue()
    internal var verificationQueue : VerificationQueue = ImmediateFailVerificationQueue(argQueue)

    fun verify(verification: () -> Unit) {
        try {
            verificationQueue = argQueue.getVerificationQueue()
            verification()
            verificationQueue.verify()
        } finally {
            verificationQueue = ImmediateFailVerificationQueue(argQueue)
        }
    }

}
