package io.aconite.data

import org.springframework.aop.framework.ProxyFactory
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.core.annotation.AnnotationUtils
import kotlin.reflect.KClass

class SuspendTransactionalBeanPostProcessor : BeanPostProcessor {
    @Autowired
    private lateinit var transactionalCoroutineExecutor: TransactionalCoroutineExecutor

    private val beans = HashMap<String, KClass<*>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val javaClazz = bean.javaClass
        val methodAnnotated = javaClazz.methods
            .any { AnnotationUtils.getAnnotation(it, SuspendTransactional::class.java) != null }
        if (methodAnnotated) {
            beans[beanName] = javaClazz.kotlin
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        beans[beanName] ?: return bean
        val proxyFactory = ProxyFactory()
        val pointcut = SuspendTransactionalMethodPointcut()
        val advice = SuspendTransactionalMethodInterceptor(transactionalCoroutineExecutor)
        val advisor = DefaultPointcutAdvisor(pointcut, advice)
        proxyFactory.addAdvisor(advisor)
        proxyFactory.setTarget(bean)
        return proxyFactory.proxy
    }
}