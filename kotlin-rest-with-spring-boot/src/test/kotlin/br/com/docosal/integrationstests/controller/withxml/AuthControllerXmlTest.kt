package br.com.docosal.integrationstests.controller.withjson

import br.com.docosal.integrationstests.TestConfigs
import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.integrationstests.vo.AccountCredentialsDTO
import br.com.docosal.integrationstests.vo.TokenDTO
import io.restassured.RestAssured
import io.restassured.filter.log.LogDetail
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class) // Usar apenas em testes de integracao
@TestInstance(Lifecycle.PER_CLASS)
class AuthControllerXmlTest : AbstractIntegrationTest(){

    private lateinit var tokenDTO: TokenDTO

    @BeforeAll
    fun setupTests() {
        tokenDTO = TokenDTO()
    }

    @Test
    @Order(1)
    fun testLoginXML() {
        var user: AccountCredentialsDTO = AccountCredentialsDTO(
            username = "Douglas",
            password = "admin123"
        )

        tokenDTO = RestAssured.given()
            .baseUri(TestConfigs.SERVER_URI)
            .port(TestConfigs.SERVER_PORT)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .body(user)
            .`when`()
            .post("/auth/signin")
            .then()
            .log()
            .ifValidationFails(LogDetail.ALL)
            .statusCode(200)
            .extract()
            .body()
            .`as`(TokenDTO::class.java)

        assertNotNull(tokenDTO.accessToken)
        assertNotNull(tokenDTO.refreshToken)
    }

    @Test
    @Order(2)
    fun testRefreshTokenXML() {
        tokenDTO = RestAssured.given()
            .baseUri(TestConfigs.SERVER_URI)
            .port(TestConfigs.SERVER_PORT)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .pathParams("username", tokenDTO.username)
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION,
                "Bearer ${tokenDTO.refreshToken}")
            .`when`()
            .put("/auth/refresh/{username}")
            .then()
            .log()
            .ifValidationFails(LogDetail.ALL)
            .statusCode(200)
            .extract()
            .body()
            .`as`(TokenDTO::class.java)

        assertNotNull(tokenDTO.accessToken)
        assertNotNull(tokenDTO.refreshToken)
    }
}