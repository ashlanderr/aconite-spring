package io.aconite.webflux

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AconiteWebFluxErrorConfiguration {
    @Bean
    open fun customHttpExceptionControllerAdvice() = CustomHttpExceptionControllerAdvice()
}