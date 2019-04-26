package io.aconite.webflux

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ExceptionConverterContinuation<T>(
    private val inner: Continuation<T>,
    private val converter: CompositeExceptionConverter
) : Continuation<T> {
    override val context = inner.context

    override fun resumeWith(result: Result<T>) {
        result.onSuccess { inner.resume(it) }
        result.onFailure { ex -> inner.resumeWithException(converter.convert(ex)) }
    }
}