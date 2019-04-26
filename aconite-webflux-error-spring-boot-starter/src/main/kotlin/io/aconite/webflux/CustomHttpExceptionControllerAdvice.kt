package io.aconite.webflux

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.ConcurrentHashMap

data class ErrorBody(
    val code: Int,
    val message: String
)

@ControllerAdvice
class CustomHttpExceptionControllerAdvice {
    private val handlerCache = ConcurrentHashMap<Class<*>, (Exception) -> ResponseEntity<ErrorBody>>()

    @ExceptionHandler(HttpException::class)
    fun handleHttp(ex: HttpException): ResponseEntity<ErrorBody> {
        if (ex.httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) throw ex
        return httpError(ex.httpStatus, ex.extCode, ex.message)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleCustom(ex: RuntimeException): ResponseEntity<ErrorBody> {
        val handler = handlerCache.computeIfAbsent(ex.javaClass, this::buildHandler)
        return handler(ex)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleStatus(ex: ResponseStatusException): ResponseEntity<ErrorBody> {
        return httpError(ex.status, message = ex.message)
    }

    private fun buildHandler(clazz: Class<*>): (Exception) -> ResponseEntity<ErrorBody> {
        val annotation = clazz.getAnnotation(HttpError::class.java)
        return if (annotation != null) {
            { ex ->
                val message = annotation.message.ifEmpty { ex.message }
                httpError(annotation.httpStatus, annotation.extCode, message)
            }
        } else {
            { ex -> throw ex }
        }
    }

    private fun httpError(
        status: HttpStatus,
        code: Int = 0,
        message: String? = null
    ): ResponseEntity<ErrorBody> {
        return ResponseEntity(ErrorBody(code, message ?: status.reasonPhrase), status)
    }
}