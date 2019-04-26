package io.aconite.webflux

import java.util.concurrent.ConcurrentHashMap

class CompositeExceptionConverter(exceptionConverters: List<RetrofitExceptionConverter>) {
    private val cache = ConcurrentHashMap<Class<out Throwable>, RetrofitExceptionConverter>()
    private val exceptionConverters = exceptionConverters + EmptyRetrofitExceptionConverter

    fun convert(exception: Throwable): Throwable {
        val converter = cache.computeIfAbsent(exception.javaClass) { key ->
            exceptionConverters.first { it.supports(key) }
        }
        return converter.convert(exception)
    }
}