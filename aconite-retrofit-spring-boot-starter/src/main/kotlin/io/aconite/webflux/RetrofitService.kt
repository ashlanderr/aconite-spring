package io.aconite.webflux

interface RetrofitService<T : Any> {
    operator fun get(baseUrl: String): T
}