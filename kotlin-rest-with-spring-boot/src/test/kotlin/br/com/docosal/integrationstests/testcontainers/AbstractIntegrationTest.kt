package br.com.docosal.integrationstests.testcontainers

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.MapPropertySource
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.lifecycle.Startables
import org.testcontainers.utility.DockerImageName
import java.util.stream.Stream

@ContextConfiguration(initializers = [AbstractIntegrationTest.Initializer::class])
open class AbstractIntegrationTest {

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

        companion object {
            private val mysql: MySQLContainer<*> = MySQLContainer("mysql:8.0.41")

            private val localstack: LocalStackContainer = LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                .withServices(LocalStackContainer.Service.SQS) // Habilita o serviço SQS

            private fun startContainers() {
                Startables.deepStart(Stream.of(mysql, localstack)).join()
            }

            private fun createConnectionConfiguration(): Map<String, Any> {
                return mapOf(
                    "spring.datasource.url" to mysql.jdbcUrl,
                    "spring.datasource.username" to mysql.username,
                    "spring.datasource.password" to mysql.password,
                    "spring.cloud.aws.sqs.endpoint" to localstack.getEndpointOverride(LocalStackContainer.Service.SQS).toString(),
                    "spring.cloud.aws.credentials.access-key" to localstack.accessKey,
                    "spring.cloud.aws.credentials.secret-key" to localstack.secretKey,
                    "spring.cloud.aws.region.static" to localstack.region
                )
            }
        }

        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            startContainers()
            val environment = applicationContext.environment
            val testcontainers = MapPropertySource(
                "testcontainers", createConnectionConfiguration()
            )
            environment.propertySources.addFirst(testcontainers)
        }
    }
}
