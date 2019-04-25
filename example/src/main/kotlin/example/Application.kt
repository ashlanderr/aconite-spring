package example

import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Application

@RestController
class ExampleController {
    @GetMapping("/")
    suspend fun example(): String {
        delay(1000)
        return "test111"
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}