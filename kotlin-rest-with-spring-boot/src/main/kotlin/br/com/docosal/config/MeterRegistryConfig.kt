package br.com.docosal.config

import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.datadog.DatadogConfig
import io.micrometer.datadog.DatadogMeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
//
//@Component
//class MeterRegistryConfig {
//    @Bean
//    fun meterRegistry(): MeterRegistry {
//        // Configure Datadog-specific settings
//        val datadogConfig: DatadogConfig = object : DatadogConfig {
//            override fun uri(): String {
//                return "https://api.us5.datadoghq.com"
//            }
//
//            override fun get(key: String): String? {
//                return null // Use the default values for other configuration options
//            }
//        }
//
//        // Create and return a DatadogMeterRegistry
//        return DatadogMeterRegistry(datadogConfig, Clock.SYSTEM)
//    }
//
//
////    var responseSizeSummary: DistributionSummary = DistributionSummary.builder("test.book")
////        .baseUnit("bytes")
////        .publishPercentileHistogram()
////        .register(registry)
//}