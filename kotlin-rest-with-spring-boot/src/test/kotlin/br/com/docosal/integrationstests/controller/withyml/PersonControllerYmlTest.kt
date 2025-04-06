package br.com.docosal.integrationstests.controller.withyml

import br.com.docosal.integrationstests.TestConfigs
import br.com.docosal.integrationstests.controller.withyml.mapper.YMLMapper
import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.integrationstests.vo.AccountCredentialsDTO
import br.com.docosal.integrationstests.vo.PersonVO
import br.com.docosal.integrationstests.vo.TokenDTO
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.EncoderConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonControllerYmlTest : AbstractIntegrationTest() {


    private lateinit var specification: RequestSpecification
    private lateinit var objectMapper: YMLMapper
    private lateinit var person: PersonVO


    @BeforeAll
    fun setup() {
        person = PersonVO()
        objectMapper = YMLMapper()
    }

    @Test
    @Order(0)
    fun `deve autenticar e gerar o jwt Token`() {
        var user: AccountCredentialsDTO = AccountCredentialsDTO(
            username = "Douglas",
            password = "admin123"
        )

        val token = RestAssured.given()
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
            .accessToken!!

        specification = RequestSpecBuilder()
            .addHeader(
                TestConfigs.HEADER_PARAM_AUTHORIZATION,
                "Bearer $token"
            )
            .setConfig(
                RestAssuredConfig
                    .config()
                    .encoderConfig(
                        EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)
                            .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)
                    )
            )
            .setPort(TestConfigs.SERVER_PORT)
            .setBasePath("/api/person/v1")
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

    }

    @Test
    @Order(1)
    fun `deve criar uma pessoa nova`() {
        mockPerson()

        val createdPerson = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(person, objectMapper)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(PersonVO::class.java, objectMapper)

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
        person = createdPerson


    }

    private fun mockPerson() {
        person.firstName = "Claudio"
        person.lastName = "Santos"
        person.address = "Brasil - Brasil"
        person.gender = "Male"

    }

    @Test
    @Order(2)
    fun `deve atualizar as informacoes de uma pessoa`() {
        person.address = "Sao Paulo - Brasil"
        var updatedPerson = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(person, objectMapper)
            .`when`()
            .put()
            .then()
            .log()
            .all()
            .statusCode(200)
            .extract()
            .response()
            .`as`(PersonVO::class.java, objectMapper)


        assertNotNull(updatedPerson.id)
        assertNotNull(updatedPerson.firstName)
        assertNotNull(updatedPerson.lastName)
        assertNotNull(updatedPerson.address)
        assertNotNull(updatedPerson.gender)

        assertTrue(updatedPerson.id > 0)

        assertEquals("Claudio", updatedPerson.firstName)
        assertEquals("Santos", updatedPerson.lastName)
        assertEquals("Sao Paulo - Brasil", updatedPerson.address)
        assertEquals("Male", updatedPerson.gender)


    }

    @Test
    @Order(3)
    fun `deve retornar a pessoa cadastrada`() {
        var createdPerson = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .pathParams("id", person.id)
            .`when`()
            .get("{id}")
            .then()
            .log()
            .all()
            .statusCode(200)
            .extract()
            .response()
            .`as`(PersonVO::class.java, objectMapper)

        assertNotNull(createdPerson.id)
        assertNotNull(createdPerson.firstName)
        assertNotNull(createdPerson.lastName)
        assertNotNull(createdPerson.address)
        assertNotNull(createdPerson.gender)

        assertTrue(createdPerson.id > 0)

        assertEquals("Claudio", createdPerson.firstName)
        assertEquals("Santos", createdPerson.lastName)
        assertEquals("Sao Paulo - Brasil", createdPerson.address)
        assertEquals("Male", createdPerson.gender)
    }

    @Test
    @Order(4)
    fun `deve deletar uma pessoa`() {
        RestAssured.given()
            .spec(specification)
            .pathParams("id", person.id)
            .`when`()
            .delete("{id}")
            .then()
            .statusCode(204)

    }

    @Test
    @Order(5)
    fun `deve retornar todas as pessoas cadastradas`() {
        var persons = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(Array<PersonVO>::class.java, objectMapper)


        var personOne = persons[0]

        assertNotNull(personOne.id)
        assertNotNull(personOne.firstName)
        assertNotNull(personOne.lastName)
        assertNotNull(personOne.address)
        assertNotNull(personOne.gender)

        assertTrue(personOne.id > 0)

        assertEquals("Douglas", personOne.firstName)
        assertEquals("Costa", personOne.lastName)
        assertEquals("Itaquaquecetuba - SP - Brasil", personOne.address)
        assertEquals("Male", personOne.gender)
    }

    @Test
    @Order(5)
    fun `deve retornar acesso negado - FindAll sem token`() {
        var specificationWithoutToken = RequestSpecBuilder()
            .setPort(TestConfigs.SERVER_PORT)
            .setBasePath("/api/person/v1")
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

        RestAssured.given()
            .spec(specificationWithoutToken)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .`when`()
            .get()
            .then()
            .statusCode(403)
            .extract()
            .response()
            .asString()

    }

}