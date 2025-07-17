package br.com.docosal.metrics

import br.com.docosal.data.vo.v1.BookDTO
import br.com.docosal.util.Constantes.Companion.KOTLIN_REST_SPRING_BOOT
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.datadog.DatadogMeterRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicReference


@Aspect
@Component
class BookMetricsAspect {

    @Autowired
    private lateinit var meterRegistry: DatadogMeterRegistry

    private val logger: Logger = LogManager.getLogger(this::class.java.name)

    @Before("execution(* br.com.docosal.services.*.*(..))")
    fun countMethodCall(joinPoint: JoinPoint) {
        val className = joinPoint.signature.declaringType.simpleName
        val methodName = joinPoint.signature.name
        logger.info("Interceptando metodo ${className}.${methodName}. Criando metrica count.")
        val counter: Counter = Counter.builder(KOTLIN_REST_SPRING_BOOT)
            .description("Calling ${className}.${methodName}")
            .tag("methodName", methodName)
            .tag("className", className)
            .tag("appMethod", "${className}.${methodName}")
            .register(meterRegistry)
        counter.increment()
    }

    @AfterReturning(
        pointcut = "execution(* br.com.docosal.services.BookService.findById(..))", // Ajuste o pointcut para o seu serviço
        returning = "result" // Captura o objeto de retorno na variável 'result'
    )
    fun recordBookPrice(joinPoint: JoinPoint, result: Any?) {
        val className = joinPoint.signature.declaringType.simpleName
        val methodName = joinPoint.signature.name

        val payload = if (result is ResponseEntity<*>) result.body else result
        val author = getAuthorFromPayload(payload)
        val price = getPriceFromPayload(payload)
        val lastBookPrice = AtomicReference<Double>(null)
        if (price != null) {
            logger.info("Preço encontrado em {}: {}. Registrando métrica.", className, price)

            // Usamos DistributionSummary para registrar valores que variam
            val summary: DistributionSummary = DistributionSummary.builder(KOTLIN_REST_SPRING_BOOT + ".distribution.summary")
                .description("Distribuição dos preços dos livros retornados")
                .baseUnit("BRL") // Moeda
                .tag("methodName", methodName)
                .tag("className", className)
                .tag("appMethod", "${className}.${methodName}")
                .tag("BookAuthor", author!!)
                .register(meterRegistry)

            // Registra o valor do preço na métrica
            summary.record(price)


            lastBookPrice.set(price)
            val gauge: Gauge = Gauge.builder(KOTLIN_REST_SPRING_BOOT + ".gauge", lastBookPrice::get)
                .description("Distribuição dos preços dos livros retornados")
                .baseUnit("BRL") // Moeda
                .tag("methodName", methodName)
                .tag("className", className)
                .tag("BookAuthor", author!!)
                .tag("appMethod", "${className}.${methodName}")
                .register(meterRegistry)

        }
    }

    private fun getPriceFromPayload(payload: Any?): Double? {
        if (payload == null) return null
        return when (payload) {
            // ✅ Estratégia 1 (Recomendada): O payload é um DTO conhecido
            is BookDTO -> payload.price
            else -> {
                logger.warn("Tipo de payload não suportado para extração de preço: {}", payload::class.simpleName)
                null
            }
        }
    }

    private fun getAuthorFromPayload(payload: Any?): String? {
        if (payload == null) return null
        return when (payload) {
            // ✅ Estratégia 1 (Recomendada): O payload é um DTO conhecido
            is BookDTO -> payload.author
            else -> {
                logger.warn("Tipo de payload não suportado para extração de preço: {}", payload::class.simpleName)
                null
            }
        }
    }

}