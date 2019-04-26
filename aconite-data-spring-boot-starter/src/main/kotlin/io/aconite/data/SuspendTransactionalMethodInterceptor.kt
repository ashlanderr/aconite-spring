package io.aconite.data

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

class SuspendTransactionalMethodInterceptor(
    private val transactional: TransactionalCoroutineExecutor
) : MethodInterceptor {
    @Suppress("UNCHECKED_CAST")
    override fun invoke(invocation: MethodInvocation): Any? {
        val continuation = invocation.arguments.last() as Continuation<Any?>
        val fn: suspend () -> Any? = {
            transactional {
                suspendCoroutine<Any?> { cont ->
                    invocation.arguments[invocation.arguments.size - 1] = cont
                    try {
                        val result = invocation.proceed()
                        if (result !== COROUTINE_SUSPENDED) {
                            cont.resume(result)
                        }
                    } catch (ex: Throwable) {
                        cont.resumeWithException(ex)
                    }
                }
            }
        }
        fn.startCoroutine(continuation)
        return COROUTINE_SUSPENDED
    }
}