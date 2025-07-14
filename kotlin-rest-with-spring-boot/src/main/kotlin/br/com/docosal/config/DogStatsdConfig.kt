package br.com.docosal.config

import com.timgroup.statsd.NonBlockingStatsDClientBuilder
import com.timgroup.statsd.StatsDClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


//@ConfigurationProperties(prefix = "datadog")
class DogStatsdConfig {
    @Value("\${datadog.hostname}")
    var hostname: String = ""

    @Value("\${datadog.port:default}")
    var port: Int = 9999

    @Value("\${datadog.prefix:default}")
    var prefix: String = ""

    val statsd: StatsDClient = NonBlockingStatsDClientBuilder()
        .port(port!!)
        .prefix(prefix)
        .hostname(hostname)
        .build()
}