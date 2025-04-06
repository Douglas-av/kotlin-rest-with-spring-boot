package br.com.docosal.integrationstests.controller.withyml

import br.com.docosal.integrationstests.TestConfigs
import br.com.docosal.integrationstests.controller.withyml.mapper.YMLMapper
import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.integrationstests.vo.AccountCredentialsDTO
import br.com.docosal.integrationstests.vo.BookDTO
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
import java.util.Date

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerYmlTest : AbstractIntegrationTest() {


    private lateinit var specification: RequestSpecification
    private lateinit var objectMapper: YMLMapper
    private lateinit var bookDTO: BookDTO


    @BeforeAll
    fun setup() {
        objectMapper = YMLMapper()
        bookDTO = BookDTO()
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
                RestAssuredConfig.config()
                    .encoderConfig(EncoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT))
            )
            .baseUri(TestConfigs.SERVER_URI)
            .port(TestConfigs.SERVER_PORT)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(user, objectMapper)
            .`when`()
            .post("/auth/signin")
            .then()
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
            .setConfig(
                RestAssuredConfig.config()
                    .encoderConfig(
                        EncoderConfig()
                            .encodeContentTypeAs(
                                TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT
                            )
                    )
            )
            .setPort(TestConfigs.SERVER_PORT)
            .setBasePath("/api/books/v1")
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

    }

    @Test
    @Order(1)
    fun `deve criar uma pessoa nova`() {
        mockBook()

        val createdBook = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(bookDTO, objectMapper)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .`as`(BookDTO::class.java, objectMapper)


        assertNotNull(createdBook.author)
        assertNotNull(createdBook.title)
        assertNotNull(createdBook.launchDate)
        assertNotNull(createdBook.price)

        assertTrue(createdBook.key > 0)

        assertEquals("Machado de Assis", createdBook.author)
        assertEquals("Memorias postumas de brás cubas", createdBook.title)
        assertEquals(20.00, createdBook.price)
        bookDTO = createdBook

    }

    private fun mockBook() {
        bookDTO.author = "Machado de Assis"
        bookDTO.title = "Memorias postumas de brás cubas"
        bookDTO.launchDate = Date()
        bookDTO.price = 20.00
    }

    @Test
    @Order(2)
    fun `deve atualizar as informacoes de uma pessoa`() {
        bookDTO.title = "Dom Casmurro"
        var updatedBook = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .body(bookDTO, objectMapper)
            .`when`()
            .put()
            .then()
            .log()
            .all()
            .statusCode(200)
            .extract()
            .response()
            .`as`(BookDTO::class.java, objectMapper)


        assertNotNull(updatedBook.author)
        assertNotNull(updatedBook.title)
        assertNotNull(updatedBook.price)
        assertNotNull(updatedBook.launchDate)

        assertTrue(updatedBook.key > 0)

        assertEquals("Machado de Assis", updatedBook.author)
        assertEquals("Dom Casmurro", updatedBook.title)
        assertEquals(20.00, updatedBook.price)


    }

    @Test
    @Order(3)
    fun `deve retornar a pessoa cadastrada`() {
        var createdBook = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .pathParams("id", bookDTO.key)
            .`when`()
            .get("{id}")
            .then()
            .log()
            .all()
            .statusCode(200)
            .extract()
            .response()
            .`as`(BookDTO::class.java, objectMapper)

        assertNotNull(createdBook.author)
        assertNotNull(createdBook.title)
        assertNotNull(createdBook.launchDate)
        assertNotNull(createdBook.price)

        assertTrue(createdBook.key > 0)

        assertEquals("Machado de Assis", createdBook.author)
        assertEquals("Dom Casmurro", createdBook.title)
        assertEquals(20.00, createdBook.price)
    }

    @Test
    @Order(4)
    fun `deve deletar uma pessoa`() {
        RestAssured.given()
            .spec(specification)
            .pathParams("id", bookDTO.key)
            .`when`()
            .delete("{id}")
            .then()
            .statusCode(204)

    }

    @Test
    @Order(5)
    fun `deve retornar todas as pessoas cadastradas`() {
        var books = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_YML)
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
            .response()
            .`as`(Array<BookDTO>::class.java, objectMapper)

        var bookOne = books[0]

        assertNotNull(bookOne.key)
        assertNotNull(bookOne.author)
        assertNotNull(bookOne.launchDate)
        assertNotNull(bookOne.price)
        assertNotNull(bookOne.title)

        assertTrue(bookOne.key > 0)

        assertEquals("Michael C. Feathers", bookOne.author)
        assertEquals("Working effectively with legacy code", bookOne.title)
        assertEquals(49.00, bookOne.price)
    }

    @Test
    @Order(5)
    fun `deve retornar acesso negado - FindAll sem token`() {
        var specificationWithoutToken = RequestSpecBuilder()
            .setConfig(
                RestAssuredConfig.config()
                    .encoderConfig(
                        EncoderConfig()
                            .encodeContentTypeAs(
                                TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT
                            )
                    )
            )
            .setPort(TestConfigs.SERVER_PORT)
            .setBasePath("/api/books/v1")
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