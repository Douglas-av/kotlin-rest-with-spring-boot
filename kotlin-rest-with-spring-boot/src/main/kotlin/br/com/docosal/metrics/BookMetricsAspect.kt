package br.com.docosal.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.datadog.DatadogMeterRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Aspect
@Component
class BookMetricsAspect {

    @Autowired
    private lateinit var meterRegistry : DatadogMeterRegistry

    private val logger : Logger = LogManager.getLogger(this::class.java.name)

    @Before("execution(* br.com.docosal.services.BookService.findAll(..))")
    fun countFindAll(joinPoint: JoinPoint) {
        val methodName = joinPoint.signature.name
        logger.info("Interceptando metodo ${methodName}.")
        val salesCounter: Counter = Counter.builder("books.callFindAll")
            .description("callFindAll")
            .tag("methodName", methodName)
            .register(meterRegistry)
        salesCounter.increment()
    }
}