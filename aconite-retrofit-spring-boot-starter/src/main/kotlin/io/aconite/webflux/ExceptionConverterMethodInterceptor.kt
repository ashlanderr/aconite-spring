package io.aconite.webflux

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import kotlin.coroutines.Continuation

class ExceptionConverterMethodInterceptor(
    private val exceptionConverter: CompositeExceptionConverter
) : MethodInterceptor {
    @Suppress("UNCHECKED_CAST")
    override fun invoke(invocation: MethodInvocation): Any? {
        val continuation = invocation.arguments.last() as Continuation<Any?>
        val converterContinuation = ExceptionConverterContinuation(continuation, exceptionConverter)
        invocation.arguments[invocation.arguments.size - 1] = converterContinuation
        return invocation.proceed()
    }
}