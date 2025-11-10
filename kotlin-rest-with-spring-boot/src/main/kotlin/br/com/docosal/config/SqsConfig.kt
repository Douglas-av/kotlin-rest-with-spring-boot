package br.com.docosal.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.awspring.cloud.sqs.operations.TemplateAcknowledgementMode
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@Configuration
class SqsConfig {

    @Bean
    fun sqsAsyncClient(): SqsAsyncClient {
        return SqsAsyncClient.builder()
            .region(Region.US_EAST_1) // Set your region
            .endpointOverride(URI.create("http://localhost:4566"))
            .credentialsProvider(
                StaticCredentialsProvider.create( // <-- Necessário para LocalStack/SDK v2
                    AwsBasicCredentials.create("fake", "fake")
                )
            )
            .build()
    }

    @Bean
    fun customMessageConverter(objectMapper: ObjectMapper): SqsMessagingMessageConverter {
        val converter = SqsMessagingMessageConverter()
        // Define o ObjectMapper do Spring Boot no conversor
        converter.setObjectMapper(objectMapper)
        // Opcional: Se você quiser que o SqsTemplate PARE de adicionar o
        // cabeçalho "JavaType" que pode confundir receptores simples.
        converter.doNotSendPayloadTypeHeader()
        return converter
    }

    @Bean
    fun defaultSqsListenerContainerFactory(sqsAsyncClient: SqsAsyncClient, customMessageConverter: SqsMessagingMessageConverter): SqsMessageListenerContainerFactory<Any> {
        return SqsMessageListenerContainerFactory
            .builder<Any>()
            .sqsAsyncClient(sqsAsyncClient)
            .configure { options ->
                // Isso garante que o listener saiba que o ACK é manual
                options.acknowledgementMode(AcknowledgementMode.MANUAL)
                options.messageConverter(customMessageConverter)
            }
            .build()
    }

    @Bean
    fun customSqsTemplate(sqsAsyncClient: SqsAsyncClient, customMessageConverter: SqsMessagingMessageConverter): SqsTemplate {
        return SqsTemplate.builder()
            .sqsAsyncClient(sqsAsyncClient)
            .messageConverter(customMessageConverter)
            .configure { TemplateAcknowledgementMode.MANUAL }
            .build()
    }


}