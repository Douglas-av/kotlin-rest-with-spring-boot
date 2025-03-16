package br.com.docosal.integrationstests.swagger

import br.com.docosal.integrationstests.ConfigsTest
import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import io.restassured.RestAssured
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SwaggerIntegrationTest() : AbstractIntegrationTest() {

    @Test
    fun shouldDisplaySwaggerUIPage() {
        val content = RestAssured.given()
            .basePath("/swagger-ui/index.html")
            .port(ConfigsTest.SERVER_PORT)
                .`when`()
            .get()
            .then()
                .statusCode(200)
            .extract()
            .body()
                .asString()

        Assertions.assertTrue(content.contains("Swagger UI"))
    }

}