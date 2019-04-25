package io.aconite.webflux

import org.springframework.aop.framework.ProxyFactory
import org.springframework.aop.support.DefaultPointcutAdvisor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController
import kotlin.reflect.KClass

class SuspendControllersBeanPostProcessor : BeanPostProcessor {
    @Autowired
    private lateinit var nettyCoroutineDispatcher: NettyCoroutineDispatcher

    private val beans = HashMap<String, KClass<*>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val clazz = bean.javaClass
        if (clazz.getAnnotation(RestController::class.java) != null) {
            beans[beanName] = clazz.kotlin
        }
        return bean
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if (!beans.containsKey(beanName)) return bean
        val proxyFactory = ProxyFactory()
        val pointcut = SuspendMappingMethodPointcut()
        val advice = SuspendMappingMethodInterceptor(nettyCoroutineDispatcher)
        val advisor = DefaultPointcutAdvisor(pointcut, advice)
        proxyFactory.addAdvisor(advisor)
        proxyFactory.setTarget(bean)
        return proxyFactory.proxy
    }
}