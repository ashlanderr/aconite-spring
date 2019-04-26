package io.aconite.webflux

interface RetrofitExceptionConverter {
    fun supports(clazz: Class<out Throwable>): Boolean
    fun convert(exception: Throwable): Throwable
}