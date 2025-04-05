package br.com.docosal.integrationstests.controller.withyml

import br.com.docosal.integrationstests.TestConfigs
import br.com.docosal.integrationstests.controller.withyml.mapper.YMLMapper
import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.integrationstests.vo.AccountCredentialsDTO
import br.com.docosal.integrationstests.vo.TokenDTO
import io.restassured.RestAssured
import io.restassured.config.EncoderConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerYmlTest : AbstractIntegrationTest(){

    private lateinit var objectMapper: YMLMapper
    private lateinit var tokenDTO: TokenDTO

    @BeforeAll
    fun setupTests(){
        tokenDTO = TokenDTO()
        objectMapper = YMLMapper()
    }

    @Test
    @Order(0)
    fun testLogin() {
        val user = AccountCredentialsDTO(
            username = "Douglas",
            password = "admin123"
        )

        tokenDTO = RestAssured.given()
            .config(
                RestAssuredConfig
                    .config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
                    )
            )
            .basePath("/auth/signin")
            .port(TestConfigs.SERVER_PORT)
            .accept(TestConfigs.CONTENT_TYPE_YML)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(user, objectMapper)
            .log()
            .ifValidationFails(LogDetail.ALL)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(TokenDTO::class.java, objectMapper)

        assertNotNull(tokenDTO.accessToken)
        assertNotNull(tokenDTO.refreshToken)
    }

    @Test
    @Order(1)
    fun testRefreshToken() {

        tokenDTO = RestAssured.given()
            .config(
                RestAssuredConfig
                    .config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
                    )
            )
            .basePath("/auth/refresh")
            .port(TestConfigs.SERVER_PORT)
            .accept(TestConfigs.CONTENT_TYPE_YML)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .log()
            .ifValidationFails(LogDetail.ALL)
            .pathParam("username", tokenDTO.username)
            .header(
                TestConfigs.HEADER_PARAM_AUTHORIZATION,
                "Bearer ${tokenDTO.refreshToken}")
            .`when`()
            .put("{username}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(TokenDTO::class.java, objectMapper)

        assertNotNull(tokenDTO.accessToken)
        assertNotNull(tokenDTO.refreshToken)
    }
}