package io.aconite.webflux

import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import reactor.core.publisher.Mono
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

class SuspendMappingMethodInterceptor(
    private val context: CoroutineContext
) : MethodInterceptor {
    override fun invoke(invocation: MethodInvocation): Any? {
        val continuation = invocation.arguments.last()
        return if (continuation is EmptyContinuation<*>) {
            Mono.create<Any?> { sink ->
                val cont = MonoContinuation(context, sink)
                invocation.arguments[invocation.arguments.size - 1] = cont
                try {
                    val result = invocation.proceed()
                    if (result !== COROUTINE_SUSPENDED) {
                        sink.success(result)
                    }
                } catch (ex: Throwable) {
                    sink.error(ex)
                }
            }
        } else {
            invocation.proceed()
        }
    }
}