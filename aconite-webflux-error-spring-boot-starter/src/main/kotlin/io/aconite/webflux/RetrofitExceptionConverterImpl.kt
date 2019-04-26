package io.aconite.webflux

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import java.io.IOException
import retrofit2.HttpException as RetrofitHttpException

class RetrofitExceptionConverterImpl(
    private val objectMapper: ObjectMapper
) : RetrofitExceptionConverter {
    override fun supports(clazz: Class<out Throwable>): Boolean {
        return clazz == RetrofitHttpException::class.java
    }

    override fun convert(exception: Throwable): Throwable {
        exception as RetrofitHttpException
        val status = HttpStatus.resolve(exception.code()) ?: HttpStatus.INTERNAL_SERVER_ERROR
        val error = try {
            val body = exception.response()?.errorBody()?.string()
            body?.let { objectMapper.readValue<ErrorBody>(body) }
        } catch (ex: IOException) {
            null
        }
        return HttpException(status, error?.code ?: 0, error?.message ?: exception.message(), exception)
    }
}