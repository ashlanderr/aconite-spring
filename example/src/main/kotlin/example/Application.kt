package example

import io.aconite.data.SuspendTransactional
import io.aconite.webflux.HttpError
import io.aconite.webflux.HttpException
import io.aconite.webflux.RetrofitServiceFactory
import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import retrofit2.http.GET
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@SpringBootApplication
class Application {
    @Bean
    fun exampleWebService(retrofitServiceFactory: RetrofitServiceFactory) =
        retrofitServiceFactory.create<ExampleWebService>("http://localhost:8080")
}

@HttpError(HttpStatus.BAD_REQUEST, 200)
class CustomException(message: String) : RuntimeException(message)

@Entity
@Table(name = "test")
data class TestEntity(
    @Id
    @Column(name = "key")
    val key: String,

    @Column(name = "value")
    val value: String
)

interface TestRepository : CrudRepository<TestEntity, String>

interface ExampleWebService {
    @GET("/")
    suspend fun call(): String
}

@Service
class TestService(
    private val testRepository: TestRepository
) {
    @SuspendTransactional
    suspend fun testTransaction(key: String): String? {
        println(Thread.currentThread())
        val result = testRepository.findByIdOrNull(key)?.value
        println(Thread.currentThread())
        return result
    }
}

@RestController
class ExampleController(
    private val webService: ExampleWebService,
    private val testService: TestService
) {
    @GetMapping("/")
    suspend fun example(): String {
        //println(Thread.currentThread())
        delay(1000)
        return "test111"
    }

    @GetMapping("/http-error")
    fun httpError(): String = throw HttpException(HttpStatus.I_AM_A_TEAPOT, 100, "Test error")

    @GetMapping("/custom-error")
    fun customError(): String = throw CustomException("test")

    @GetMapping("/call-self")
    suspend fun callSelf() = webService.call()

    @GetMapping("/jpa-test/{key}")
    suspend fun jpaTest(@PathVariable key: String): String? {
        //println(Thread.currentThread())
        delay(1000)
        //println(Thread.currentThread())
        val result = testService.testTransaction(key)
        //println(Thread.currentThread())
        return result
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}