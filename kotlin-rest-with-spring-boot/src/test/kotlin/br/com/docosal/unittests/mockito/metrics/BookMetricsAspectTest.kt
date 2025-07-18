package br.com.docosal.unittests.mockito.metrics

import io.micrometer.datadog.DatadogMeterRegistry
import org.mockito.Mock

class BookMetricsAspectTest {

    @Mock
    private lateinit var meterRegistry: DatadogMeterRegistry


}