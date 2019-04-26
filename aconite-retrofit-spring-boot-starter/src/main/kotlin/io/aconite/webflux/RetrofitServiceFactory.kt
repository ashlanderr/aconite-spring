package io.aconite.webflux

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.reactive.function.client.WebClient

class RetrofitServiceFactory(
    private val objectMapper: ObjectMapper,
    private val webClient: WebClient,
    exceptionConverters: List<RetrofitExceptionConverter>
) {
    private val exceptionConverter = CompositeExceptionConverter(exceptionConverters)

    fun <T : Any> create(clazz: Class<T>): RetrofitService<T> = RetrofitServiceImpl(
        clazz,
        objectMapper,
        exceptionConverter,
        webClient
    )

    inline fun <reified T : Any> create() = create(T::class.java)

    fun <T : Any> create(clazz: Class<T>, baseUrl: String) = create(clazz)[baseUrl]
    inline fun <reified T : Any> create(baseUrl: String) = create(T::class.java, baseUrl)
}