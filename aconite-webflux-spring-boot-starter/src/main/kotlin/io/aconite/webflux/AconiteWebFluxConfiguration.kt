package io.aconite.webflux

import com.fasterxml.classmate.TypeResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux

@Configuration
@EnableSwagger2WebFlux
open class AconiteWebFluxConfiguration : WebFluxConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/swagger-ui.html**")
            .addResourceLocations("classpath:/META-INF/resources/")

        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
    }

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