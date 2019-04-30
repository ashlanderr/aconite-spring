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
    override suspend operator fun <T> invoke(block: suspend () -> T): T {
        return suspendCoroutine { cont ->
            executor.execute {
                transactionTemplate.execute { tx ->
                    runBlocking {
                        try {
                            val result = block()
                            cont.resume(result)
                        } catch (ex: Throwable) {
                            tx.setRollbackOnly()
                            cont.resumeWithException(ex)
                        }
                    }
                }
            }
        }
    }
}