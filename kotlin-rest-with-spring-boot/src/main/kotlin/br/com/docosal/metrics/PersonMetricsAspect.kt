package br.com.docosal.metrics

import br.com.docosal.config.DogStatsdConfig
import br.com.docosal.util.Constantes.Companion.METRIC_INCREMENTCOUNTER_TAG
import br.com.docosal.util.Constantes.Companion.METRIC_PERSON_TAG
import org.apache.logging.log4j.LogManager
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

@Aspect
@Component
class PersonMetricsAspect {
//    @Autowired
//    private lateinit var personStatsdClient : DogStatsdConfig
//
//    private val logger : Logger = LogManager.getLogger(this::class.java.name)
//
//    @Before("execution(* br.com.docosal.services.PersonService.findAll(..))")
//    fun countFindAll(joinPoint: JoinPoint) {
//        var methodName = joinPoint.signature.name
//        logger.info("Interceptando metodo ${methodName}.")
//        personStatsdClient.statsd.incrementCounter("${METRIC_INCREMENTCOUNTER_TAG}.${methodName}")
//        println("hostname= ${personStatsdClient.port}")
//    }

}