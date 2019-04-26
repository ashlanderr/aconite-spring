package io.aconite.webflux

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext

class EmptyContinuation<T> : Continuation<T> {
    override val context = EmptyCoroutineContext
    override fun resumeWith(result: Result<T>) {}
}