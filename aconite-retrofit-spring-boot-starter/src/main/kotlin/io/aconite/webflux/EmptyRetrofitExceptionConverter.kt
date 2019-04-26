package io.aconite.webflux

object EmptyRetrofitExceptionConverter : RetrofitExceptionConverter {
    override fun supports(clazz: Class<out Throwable>) = true
    override fun convert(exception: Throwable) = exception
}