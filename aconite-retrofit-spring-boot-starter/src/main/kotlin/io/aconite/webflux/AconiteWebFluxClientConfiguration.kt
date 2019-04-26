package io.aconite.webflux

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
open class AconiteWebFluxClientConfiguration {
    @Autowired(required = false)
    private lateinit var exceptionConverters: List<RetrofitExceptionConverter>

    @Bean
    @ConditionalOnMissingBean
    open fun aconiteWebClient() = WebClient.builder()
        .build()

    @Bean
    open fun retrofitServiceFactory(objectMapper: ObjectMapper) = RetrofitServiceFactory(
        objectMapper,
        aconiteWebClient(),
        exceptionConverters
    )
}