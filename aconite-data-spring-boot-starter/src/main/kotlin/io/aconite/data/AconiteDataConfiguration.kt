package io.aconite.data

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.Executor
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Configuration
@EnableConfigurationProperties(AconiteDataProperties::class)
open class AconiteDataConfiguration {
    @Autowired
    private lateinit var properties: AconiteDataProperties

    @Bean
    @ConditionalOnMissingBean
    open fun transactionalTaskExecutor(): Executor = ThreadPoolExecutor(
        0, properties.transactionalPoolSize,
        60, TimeUnit.SECONDS,
        SynchronousQueue()
    )

    @Bean
    @ConditionalOnMissingBean
    open fun transactionalCoroutineExecutor(
        transactionTemplate: TransactionTemplate
    ): TransactionalCoroutineExecutor = DefaultTransactionalCoroutineExecutor(
        transactionalTaskExecutor(),
        transactionTemplate
    )

    @Bean
    open fun suspendTransactionalBeanPostProcessor() = SuspendTransactionalBeanPostProcessor()
}