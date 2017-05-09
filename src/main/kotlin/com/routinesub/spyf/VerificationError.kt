package com.routinesub.spyf

class VerificationError (times: Times, vararg matchers: Matcher<*>) : AssertionError(
        "Expected call matching (${matchers.map { it.describe }.joinToString(",")}) ${times}"
)
