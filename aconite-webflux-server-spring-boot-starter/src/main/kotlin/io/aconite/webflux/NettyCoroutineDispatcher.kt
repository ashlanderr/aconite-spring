package io.aconite.webflux

import io.netty.channel.EventLoopGroup
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

class NettyCoroutineDispatcher(
    private val eventLoopGroup: EventLoopGroup
) : CoroutineDispatcher(), Delay {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        eventLoopGroup.execute(block)
    }

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        eventLoopGroup.schedule({ continuation.resume(Unit) }, timeMillis, TimeUnit.MILLISECONDS)
    }

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
        val future = eventLoopGroup.schedule(block, timeMillis, TimeUnit.MILLISECONDS)
        return DisposableHandle { future.cancel(true) }
    }
}