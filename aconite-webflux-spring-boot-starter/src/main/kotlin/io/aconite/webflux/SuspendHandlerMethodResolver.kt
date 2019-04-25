package io.aconite.webflux

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import org.springframework.web.method.HandlerMethod
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

class SuspendHandlerMethodResolver(
    private val typeResolver: TypeResolver
) : HandlerMethodResolver(typeResolver) {
    override fun methodReturnType(handlerMethod: HandlerMethod): ResolvedType {
        val type = handlerMethod.method.kotlinFunction?.returnType?.javaType
        return type?.let { typeResolver.resolve(it) } ?: super.methodReturnType(handlerMethod)
    }
}