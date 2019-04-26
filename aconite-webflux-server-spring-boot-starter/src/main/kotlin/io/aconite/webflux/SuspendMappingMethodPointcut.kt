package io.aconite.webflux

import org.springframework.aop.support.StaticMethodMatcherPointcut
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.bind.annotation.RequestMapping
import java.lang.reflect.Method
import kotlin.reflect.jvm.kotlinFunction

class SuspendMappingMethodPointcut : StaticMethodMatcherPointcut() {
    override fun matches(method: Method, targetClass: Class<*>): Boolean {
        if (AnnotationUtils.getAnnotation(method, RequestMapping::class.java) == null) return false
        return method.kotlinFunction?.isSuspend ?: false
    }
}