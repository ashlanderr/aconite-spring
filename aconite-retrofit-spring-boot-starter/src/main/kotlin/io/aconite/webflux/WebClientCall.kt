package io.aconite.webflux

import okhttp3.*
import okio.Buffer
import okio.Timeout
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.Disposable
import java.io.IOException

class WebClientCall(
    private val webClient: WebClient,
    private val request: Request
) : Call {
    private var executed = false
    private var cancelled = false
    private var handle: Disposable? = null

    class Factory(private val webClient: WebClient) : Call.Factory {
        override fun newCall(request: Request) = WebClientCall(webClient, request)
    }

    override fun enqueue(responseCallback: Callback) {
        executed = true

        val builder = webClient.method(HttpMethod.valueOf(request.method()))
            .uri(request.url().uri())
            .accept(MediaType.ALL)

        for ((header, values) in request.headers().toMultimap()) {
            builder.header(header, *values.toTypedArray())
        }

        request.body()?.let { body ->
            body.contentType()?.let { type ->
                builder.contentType(MediaType.parseMediaType(type.toString()))
            }
            builder.contentLength(body.contentLength())
            val data = Buffer()
            body.writeTo(data)
            builder.bodyValue(data.readByteArray())
        }

        val bodyFlux = builder.exchange()
            .flatMap { it.toEntity(String::class.java) }

        handle = bodyFlux.subscribe({ respEntity ->
            val respBuilder = Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1) // todo
                .code(respEntity.statusCodeValue)
                .message(respEntity.statusCode.reasonPhrase)
            val body = respEntity.body ?: ""
            val mediaType = respEntity.headers.contentType?.let { okhttp3.MediaType.get(it.toString()) }
            val respBody = ResponseBody.create(mediaType, body)
            respBuilder.body(respBody)
            for ((header, values) in respEntity.headers) {
                for (value in values) {
                    respBuilder.addHeader(header, value)
                }
            }
            responseCallback.onResponse(this, respBuilder.build())
        }, { ex ->
            responseCallback.onFailure(this, IOException(ex))
        })
    }

    override fun isExecuted() = executed
    override fun request() = request
    override fun isCanceled() = cancelled
    override fun clone() = WebClientCall(webClient, request)

    override fun cancel() {
        if (!cancelled) {
            cancelled = true
            handle?.dispose()
        }
    }

    override fun timeout(): Timeout {
        TODO("not implemented")
    }

    override fun execute(): Response {
        TODO("not implemented")
    }
}