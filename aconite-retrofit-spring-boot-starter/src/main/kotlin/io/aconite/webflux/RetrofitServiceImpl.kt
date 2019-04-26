package io.aconite.webflux

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.aop.Pointcut
import org.springframework.aop.framework.ProxyFactory
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.web.reactive.function.client.WebClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.ConcurrentHashMap

internal class RetrofitServiceImpl<T : Any>(
    private val clazz: Class<T>,
    private val objectMapper: ObjectMapper,
    private val exceptionConverters: CompositeExceptionConverter,
    private val webClient: WebClient
) : RetrofitService<T> {
    private val cache = ConcurrentHashMap<String, T>()

    override fun get(baseUrl: String): T = cache.computeIfAbsent(baseUrl) { key ->
        val retrofit = Retrofit.Builder()
            .baseUrl(key)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .callFactory(WebClientCall.Factory(webClient))
            .build()
        wrap(retrofit.create(clazz))
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: Any> wrap(obj: T): T {
        val proxyFactory = ProxyFactory()
        val advice = ExceptionConverterMethodInterceptor(exceptionConverters)
        val advisor = DefaultPointcutAdvisor(Pointcut.TRUE, advice)
        proxyFactory.addAdvisor(advisor)
        proxyFactory.setTarget(obj)
        return proxyFactory.proxy as T
    }
}