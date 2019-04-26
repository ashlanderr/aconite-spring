package io.aconite.data

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("io.aconite.data")
class AconiteDataProperties {
    var transactionalPoolSize: Int = Runtime.getRuntime().availableProcessors() * 2
}