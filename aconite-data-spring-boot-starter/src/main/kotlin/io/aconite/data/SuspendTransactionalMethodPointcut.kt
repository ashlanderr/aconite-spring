package io.aconite.data

import org.springframework.aop.support.StaticMethodMatcherPointcut
import org.springframework.core.annotation.AnnotationUtils
import java.lang.reflect.Method

class SuspendTransactionalMethodPointcut : StaticMethodMatcherPointcut() {
    override fun matches(method: Method, targetClass: Class<*>): Boolean {
        return AnnotationUtils.getAnnotation(method, SuspendTransactional::class.java) != null
    }
}