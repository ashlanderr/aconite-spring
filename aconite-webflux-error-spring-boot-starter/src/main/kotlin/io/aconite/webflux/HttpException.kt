package io.aconite.webflux

import org.springframework.http.HttpStatus

annotation class HttpError(
    val httpStatus: HttpStatus,
    val extCode: Int = 0,
    val message: String = ""
)

class HttpException(
    val httpStatus: HttpStatus,
    val extCode: Int = 0,
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(
    message,
    cause
) {
    override fun toString(): String {
        return "HttpException(" +
            "status=$httpStatus, " +
            "extCode=$extCode, " +
            "message='$message')"
    }
}