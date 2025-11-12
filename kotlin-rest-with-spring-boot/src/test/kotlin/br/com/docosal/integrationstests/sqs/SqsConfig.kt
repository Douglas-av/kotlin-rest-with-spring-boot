package br.com.docosal.integrationstests.sqs

import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI

@TestConfiguration
class SqsConfig {

    private lateinit var localstack: LocalStackContainer

        @Bean
        @Primary
        fun sqsAsyncClient(): SqsAsyncClient {
            return SqsAsyncClient.builder()
                .endpointOverride(URI.create(localstack.getEndpointOverride(LocalStackContainer.Service.SQS).toString()))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(localstack.accessKey, localstack.secretKey)))
                .build()
        }

        @Bean
        fun customMessageConverter(): SqsMessagingMessageConverter {
            return SqsMessagingMessageConverter()
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

}
