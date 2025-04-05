package br.com.docosal.integrationstests.controller.cors.withjson

import br.com.docosal.integrationstests.TestConfigs
import br.com.docosal.integrationstests.vo.PersonVO
import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.integrationstests.vo.AccountCredentialsDTO
import br.com.docosal.integrationstests.vo.TokenDTO
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.*
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class) // Usar apenas em testes de integracao
@TestInstance(Lifecycle.PER_CLASS)
class PersonControllerCorsWithJson() : AbstractIntegrationTest() {

    private lateinit var specification: RequestSpecification
    private lateinit var objectMapper: ObjectMapper
    private lateinit var person: PersonVO
    private lateinit var token: String

    @BeforeAll
    fun setupTests() {
        objectMapper = ObjectMapper()
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        person = PersonVO()
        token = ""
    }

    @Test
    @Order(0)
    fun authenticate() {
        var user: AccountCredentialsDTO = AccountCredentialsDTO(
            username = "Douglas",
            password = "admin123"
        )

        token = RestAssured.given()
            .baseUri(TestConfigs.SERVER_URI)
            .port(TestConfigs.SERVER_PORT)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .header(
                TestConfigs.HEADER_PARAM_ORIGIN,
                TestConfigs.ORIGIN_LOCAL_HOST
            )
            .body(user)
            .`when`()
            .post("/auth/signin")
            .then()
            .log()
            .ifValidationFails(LogDetail.BODY)
            .statusCode(200)
            .extract()
            .body()
            .`as`(TokenDTO::class.java)
            .accessToken!!
    }


    @Test
    @Order(1)
    fun testCreate() {
        mockPerson()

        specification = RequestSpecBuilder()
            .addHeader(
                TestConfigs.HEADER_PARAM_ORIGIN,
                TestConfigs.ORIGIN_LOCAL_HOST
            )
            .addHeader(
                TestConfigs.HEADER_PARAM_AUTHORIZATION,
                "Bearer $token"
            )
            .setBasePath("/api/person/v1")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

        val content = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .body(person)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString()

        val createdPerson = objectMapper.readValue(
            content,
            PersonVO::class.java
        )
        person = createdPerson

        Assertions.assertNotNull(createdPerson.id)
        Assertions.assertNotNull(createdPerson.firstName)
        Assertions.assertNotNull(createdPerson.lastName)
        Assertions.assertNotNull(createdPerson.address)
        Assertions.assertNotNull(createdPerson.gender)

        Assertions.assertTrue(createdPerson.id > 0)

        Assertions.assertEquals("Douglas", createdPerson.firstName)
        Assertions.assertEquals("Alves", createdPerson.lastName)
        Assertions.assertEquals("SP, SP - Brasil", createdPerson.address)
        Assertions.assertEquals("Male", createdPerson.gender)
    }

    @Test
    @Order(2)
    fun testCreateWithWrongOrigin() {
        mockPerson()

        specification = RequestSpecBuilder()
            .addHeader(
                TestConfigs.HEADER_PARAM_ORIGIN,
                TestConfigs.ORIGIN_GOOGLE
            )
            .addHeader(
                TestConfigs.HEADER_PARAM_AUTHORIZATION,
                "Bearer $token"
            )
            .setBasePath("/api/person/v1")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

        val content = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .body(person)
            .`when`()
            .post()
            .then()
            .statusCode(403)
            .extract()
            .body()
            .asString()

        Assertions.assertEquals("Invalid CORS request", content)
    }

    @Test
    @Order(3)
    fun testFindById() {
        mockPerson()

        specification = RequestSpecBuilder()
            .addHeader(
                TestConfigs.HEADER_PARAM_ORIGIN,
                TestConfigs.ORIGIN_LOCAL_HOST
            )
            .addHeader(
                TestConfigs.HEADER_PARAM_AUTHORIZATION,
                "Bearer $token"
            )
            .setBasePath("/api/person/v1")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

        val content = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .pathParams("id", person.id)
            .`when`()["{id}"]
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString()

        Assertions.assertNotNull(person.id)
        Assertions.assertNotNull(person.firstName)
        Assertions.assertNotNull(person.lastName)
        Assertions.assertNotNull(person.address)
        Assertions.assertNotNull(person.gender)

        Assertions.assertTrue(person.id > 0)

        Assertions.assertEquals("Douglas", person.firstName)
        Assertions.assertEquals("Alves", person.lastName)
        Assertions.assertEquals("SP, SP - Brasil", person.address)
        Assertions.assertEquals("Male", person.gender)
    }

    @Test
    @Order(2)
    fun findByIdWithWrongOrigin() {
        mockPerson()

        specification = RequestSpecBuilder()
            .addHeader(
                TestConfigs.HEADER_PARAM_ORIGIN,
                TestConfigs.ORIGIN_GOOGLE
            )
            .addHeader(
                TestConfigs.HEADER_PARAM_AUTHORIZATION,
                "Bearer $token"
            )
            .setBasePath("/api/person/v1")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

        val content = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .pathParams("id", person.id)
            .`when`()["{id}"]
            .then()
            .statusCode(403)
            .extract()
            .body()
            .asString()

        Assertions.assertEquals("Invalid CORS request", content)
    }

    private fun mockPerson() {
        person.firstName = "Douglas"
        person.lastName = "Alves"
        person.address = "SP, SP - Brasil"
        person.gender = "Male"

    }

}