package br.com.docosal.integrationstests.controller.withjson

import br.com.docosal.integrationstests.TestConfigs
import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.integrationstests.vo.AccountCredentialsDTO
import br.com.docosal.integrationstests.vo.PersonVO
import br.com.docosal.integrationstests.vo.TokenDTO
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonControllerJsonTest : AbstractIntegrationTest() {


    private lateinit var specification: RequestSpecification
    private lateinit var objectMapper : ObjectMapper
    private lateinit var person: PersonVO


    @BeforeAll
    fun setup(){
        objectMapper = ObjectMapper()
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        person = PersonVO()
    }

    @Test
    @Order(0)
    fun `deve autenticar e gerar o jwt Token`(){
        var user: AccountCredentialsDTO = AccountCredentialsDTO(
            username = "Douglas",
            password = "admin123"
        )

        val token = RestAssured.given()
            .baseUri(TestConfigs.SERVER_URI)
            .port(TestConfigs.SERVER_PORT)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
            .accessToken!!

        specification = RequestSpecBuilder()
            .addHeader(
                TestConfigs.HEADER_PARAM_AUTHORIZATION,
                "Bearer $token"
            )
            .setPort(TestConfigs.SERVER_PORT)
            .setBasePath("/api/person/v1")
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

    }

    @Test
    @Order(1)
    fun `deve criar uma pessoa nova`(){
        mockPerson()

        val content = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .body(person)
            .`when`()
            .log()
            .ifValidationFails(LogDetail.ALL)
            .log().all()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString()

        val createdPerson = objectMapper.readValue(content, PersonVO::class.java)

        assertNotNull(createdPerson.id)
        assertNotNull(createdPerson.firstName)
        assertNotNull(createdPerson.lastName)
        assertNotNull(createdPerson.address)
        assertNotNull(createdPerson.gender)

        assertTrue(createdPerson.id > 0)

        assertEquals("Claudio", createdPerson.firstName)
        assertEquals("Santos", createdPerson.lastName)
        assertEquals("Brasil - Brasil", createdPerson.address)
        assertEquals("Male", createdPerson.gender)
        person.id = createdPerson.id


    }

    private fun mockPerson() {
        person.firstName = "Claudio"
        person.lastName = "Santos"
        person.address = "Brasil - Brasil"
        person.gender = "Male"

    }


    @Test
    @Order(2)
    fun `deve retornar a pessoa cadastrada`(){
        var response = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .pathParams("id", person.id)
            .`when`()
            .get("{id}")
            .then()
            .log()
            .all()
            .statusCode(200)
            .extract()
            .response()
            .asString()

        val createdPerson = objectMapper.readValue(response, PersonVO::class.java)
        println(createdPerson)

        assertNotNull(createdPerson.id)
        assertNotNull(createdPerson.firstName)
        assertNotNull(createdPerson.lastName)
        assertNotNull(createdPerson.address)
        assertNotNull(createdPerson.gender)

        assertTrue(createdPerson.id > 0)

        assertEquals("Claudio", createdPerson.firstName)
        assertEquals("Santos", createdPerson.lastName)
        assertEquals("Brasil - Brasil", createdPerson.address)
        assertEquals("Male", createdPerson.gender)
    }

}