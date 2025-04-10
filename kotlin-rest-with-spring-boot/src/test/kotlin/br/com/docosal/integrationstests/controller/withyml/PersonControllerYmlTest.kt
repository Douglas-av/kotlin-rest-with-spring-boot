package br.com.docosal.integrationstests.controller.withyml

import br.com.docosal.integrationstests.TestConfigs
import br.com.docosal.integrationstests.controller.withyml.mapper.YMLMapper
import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.integrationstests.vo.AccountCredentialsDTO
import br.com.docosal.integrationstests.vo.PersonVO
import br.com.docosal.integrationstests.vo.TokenDTO
import br.com.docosal.integrationstests.vo.wrappers.WrapperPersonVO
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
    fun `POST deve autenticar e gerar o jwt Token`() {
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
    fun `CREATE deve criar uma pessoa nova`() {
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
        assertEquals(true, createdPerson.enabled)
        person = createdPerson


    }

    private fun mockPerson() {
        person.firstName = "Claudio"
        person.lastName = "Santos"
        person.address = "Brasil - Brasil"
        person.gender = "Male"
        person.enabled = true

    }

    @Test
    @Order(2)
    fun `PUT deve atualizar as informacoes de uma pessoa`() {
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
        assertEquals(true, updatedPerson.enabled)

    }

    @Test
    @Order(3)
    fun `PATCH deve desabilitar uma pessoa`() {
        var updatedPerson = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .pathParams("id", person.id)
            .`when`()
            .patch("{id}")
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
        assertEquals(false, updatedPerson.enabled)

    }

    @Test
    @Order(4)
    fun `GET deve retornar a pessoa cadastrada`() {
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
        assertEquals(false, createdPerson.enabled)
    }

    @Test
    @Order(5)
    fun `DELETE deve deletar uma pessoa`() {
        RestAssured.given()
            .spec(specification)
            .pathParams("id", person.id)
            .`when`()
            .delete("{id}")
            .then()
            .statusCode(204)

    }

    @Test
    @Order(6)
    fun `GET deve retornar todas as pessoas cadastradas`() {
        var wrapper = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .queryParams("page", 3, "size", 12, "direction", "asc")
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(WrapperPersonVO::class.java, objectMapper)


        val persons = wrapper.embeded!!.persons

        var personOne = persons?.get(0)

        assertNotNull(personOne!!.id)
        assertNotNull(personOne.firstName)
        assertNotNull(personOne.lastName)
        assertNotNull(personOne.address)
        assertNotNull(personOne.gender)

        assertTrue(personOne.id > 0)

        assertEquals("Allin", personOne.firstName)
        assertEquals("Emmot", personOne.lastName)
        assertEquals("7913 Lindbergh Way", personOne.address)
        assertEquals("Male", personOne.gender)
        assertEquals(false, personOne.enabled)
    }

    @Test
    @Order(7)
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

    @Test
    @Order(8)
    fun `GET deve retornar todas as pessoas cadastradas que contem a string especifica no nome`() {
        var wrapper = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .pathParam("firstName", "doug")
            .queryParams("page", 0, "size", 12, "direction", "asc")
            .`when`()
            .get("findPersonByName/{firstName}")
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(WrapperPersonVO::class.java, objectMapper)


        val persons = wrapper.embeded!!.persons

        var personOne = persons?.get(0)

        assertNotNull(personOne!!.id)
        assertNotNull(personOne.firstName)
        assertNotNull(personOne.lastName)
        assertNotNull(personOne.address)
        assertNotNull(personOne.gender)

        assertTrue(personOne.id > 0)

        assertEquals("Douglas", personOne.firstName)
        assertEquals("Costa", personOne.lastName)
        assertEquals("Itaquaquecetuba - SP - Brasil", personOne.address)
        assertEquals("Male", personOne.gender)
        assertEquals(true, personOne.enabled)
    }

    @Test
    @Order(9)
    fun testHATEOAS() {
        val content = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .queryParams(
                "page", 3,
                "size",12,
                "direction", "asc")
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString()

        assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8888/api/person/v1/799"}}}"""))
        assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8888/api/person/v1/201"}}}"""))
        assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8888/api/person/v1/947"}}}"""))
        assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8888/api/person/v1/571"}}}"""))

        assertTrue(content.contains("""{"first":{"href":"http://localhost:8888/api/person/v1?direction=asc&page=0&size=12&sort=firstName,asc"}"""))
        assertTrue(content.contains(""","prev":{"href":"http://localhost:8888/api/person/v1?direction=asc&page=2&size=12&sort=firstName,asc"}"""))
        assertTrue(content.contains(""","self":{"href":"http://localhost:8888/api/person/v1?direction=asc&page=3&size=12&sort=firstName,asc"}"""))
        assertTrue(content.contains(""","next":{"href":"http://localhost:8888/api/person/v1?direction=asc&page=4&size=12&sort=firstName,asc"}"""))
        assertTrue(content.contains(""","last":{"href":"http://localhost:8888/api/person/v1?direction=asc&page=83&size=12&sort=firstName,asc"}"""))

        assertTrue(content.contains(""""page":{"size":12,"totalElements":1005,"totalPages":84,"number":3}}"""))
    }

}