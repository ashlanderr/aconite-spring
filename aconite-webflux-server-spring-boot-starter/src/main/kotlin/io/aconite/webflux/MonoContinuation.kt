package io.aconite.webflux

import reactor.core.publisher.MonoSink
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

class MonoContinuation<T>(
    override val context: CoroutineContext,
    private val mono: MonoSink<T>
) : Continuation<T> {
    override fun resumeWith(result: Result<T>) {
        result.onSuccess(mono::success)
        result.onFailure(mono::error)
    }
}