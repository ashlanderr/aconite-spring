package io.aconite.webflux

import com.fasterxml.classmate.TypeResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
open class AconiteWebFluxConfiguration : WebFluxConfigurer {
    @Bean
    @Primary // overrides springfox default HandlerMethodResolver
    open fun suspendHandlerMethodResolver(typeResolver: TypeResolver) = SuspendHandlerMethodResolver(typeResolver)

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(ContinuationArgumentResolver())
    }

    @Bean
    open fun nettyCoroutineDispatcher(reactorServerResourceFactory: ReactorResourceFactory): NettyCoroutineDispatcher {
        val loopGroup = reactorServerResourceFactory.loopResources.onServer(true)
        return NettyCoroutineDispatcher(loopGroup)
    }

    @Bean
    open fun suspendControllersBeanPostProcessor() = SuspendControllersBeanPostProcessor()
}