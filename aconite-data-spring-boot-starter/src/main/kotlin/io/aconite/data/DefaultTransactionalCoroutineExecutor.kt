package io.aconite.data

import kotlinx.coroutines.runBlocking
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DefaultTransactionalCoroutineExecutor(
    private val executor: Executor,
    private val transactionTemplate: TransactionTemplate
) : TransactionalCoroutineExecutor {
    @Suppress("UNCHECKED_CAST")
    override suspend operator fun <T> invoke(block: suspend () -> T): T {
        return suspendCoroutine { cont ->
            executor.execute {
                val (result, ex) = transactionTemplate.execute { tx ->
                    runBlocking {
                        try {
                            Pair(block(), null)
                        } catch (ex: Throwable) {
                            tx.setRollbackOnly()
                            Pair(null, ex)
                        }
                    }
                }!!
                if (ex != null) {
                    cont.resumeWithException(ex)
                } else {
                    cont.resume(result as T)
                }
            }
        }
    }
}