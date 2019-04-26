package io.aconite.webflux

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AconiteWebFluxErrorConfiguration {
    @Bean
    @ConditionalOnClass(NettyCoroutineDispatcher::class)
    open fun customHttpExceptionControllerAdvice() = CustomHttpExceptionControllerAdvice()

    @Bean
    @ConditionalOnClass(RetrofitServiceFactory::class)
    open fun retrofitExceptionConverter(objectMapper: ObjectMapper) = RetrofitExceptionConverterImpl(objectMapper)
}