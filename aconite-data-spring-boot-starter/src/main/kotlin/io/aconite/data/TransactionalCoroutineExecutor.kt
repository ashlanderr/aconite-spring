package io.aconite.data

interface TransactionalCoroutineExecutor {
    suspend operator fun <T> invoke(block: suspend () -> T): T
}