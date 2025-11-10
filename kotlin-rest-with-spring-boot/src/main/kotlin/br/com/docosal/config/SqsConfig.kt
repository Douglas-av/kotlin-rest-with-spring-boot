package br.com.docosal.config

import io.awspring.cloud.sqs.operations.SqsTemplate
import io.awspring.cloud.sqs.operations.TemplateAcknowledgementMode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SqsConfig {

    @Bean
    fun customSqsTemplate(): SqsTemplate {
        return SqsTemplate.builder()
            .configure { sqsTemplateOptions -> sqsTemplateOptions.acknowledgementMode(TemplateAcknowledgementMode.MANUAL) }
            .build()
    }
}