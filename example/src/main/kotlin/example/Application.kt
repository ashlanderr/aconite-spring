package example

import io.aconite.webflux.HttpError
import io.aconite.webflux.HttpException
import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Application

@HttpError(HttpStatus.BAD_REQUEST, 200)
class CustomException(message: String) : RuntimeException(message)

@RestController
class ExampleController {
    @GetMapping("/")
    suspend fun example(): String {
        delay(1000)
        return "test111"
    }

    @GetMapping("/http-error")
    fun httpError(): String = throw HttpException(HttpStatus.I_AM_A_TEAPOT, 100, "Test error")

    @GetMapping("/custom-error")
    fun customError(): String = throw CustomException("test")
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}