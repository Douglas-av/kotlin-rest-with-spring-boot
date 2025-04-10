package br.com.docosal.integrationstests.controller.withxml

import br.com.docosal.integrationstests.TestConfigs
import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.integrationstests.vo.AccountCredentialsDTO
import br.com.docosal.integrationstests.vo.BookDTO
import br.com.docosal.integrationstests.vo.TokenDTO
import br.com.docosal.integrationstests.vo.wrappers.WrapperBookDTO
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.LogDetail
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerXmlTest : AbstractIntegrationTest() {


    private lateinit var specification: RequestSpecification
    private lateinit var objectMapper: XmlMapper
    private lateinit var bookDTO: BookDTO


    @BeforeAll
    fun setup() {
        objectMapper = XmlMapper()
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
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
            .baseUri(TestConfigs.SERVER_URI)
            .port(TestConfigs.SERVER_PORT)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .body(user)
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
            .setPort(TestConfigs.SERVER_PORT)
            .setBasePath("/api/books/v1")
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

    }

    @Test
    @Order(1)
    fun `deve criar um livro novo`() {
        mockBook()
        val bookXml = objectMapper.writeValueAsString(bookDTO)

        val content = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .accept(TestConfigs.CONTENT_TYPE_XML)
            .body(bookXml)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString()

        val createdBook = objectMapper.readValue(content, BookDTO::class.java)

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
        val bookXml = objectMapper.writeValueAsString(bookDTO)

        var response = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .accept(TestConfigs.CONTENT_TYPE_XML)
            .body(bookXml)
            .`when`()
            .put()
            .then()
            .log()
            .all()
            .statusCode(200)
            .extract()
            .response()
            .asString()

        val updatedBook = objectMapper.readValue(response, BookDTO::class.java)

        bookDTO = updatedBook

        assertNotNull(updatedBook.author)
        assertNotNull(updatedBook.title)
        assertNotNull(updatedBook.launchDate)
        assertNotNull(updatedBook.price)

        assertTrue(updatedBook.key > 0)

        assertEquals("Machado de Assis", updatedBook.author)
        assertEquals("Dom Casmurro", updatedBook.title)
        assertEquals(20.00, updatedBook.price)

    }

    @Test
    @Order(3)
    fun `deve retornar a pessoa cadastrada`() {
        var response = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .accept(TestConfigs.CONTENT_TYPE_XML)
            .pathParams("id", bookDTO.key)
            .`when`()
            .get("{id}")
            .then()
            .log()
            .all()
            .statusCode(200)
            .extract()
            .response()
            .asString()

        val createdBook = objectMapper.readValue(response, BookDTO::class.java)

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
        var response = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .accept(TestConfigs.CONTENT_TYPE_XML)
            .queryParams("page", 1, "size", 12, "direction", "asc")
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
            .response()
            .asString()

        val wrapper = objectMapper.readValue(response, WrapperBookDTO::class.java)
        val books = wrapper.content

        var bookOne = books?.get(0)

        assertNotNull(bookOne!!.key)
        assertNotNull(bookOne.author)
        assertNotNull(bookOne.launchDate)
        assertNotNull(bookOne.price)
        assertNotNull(bookOne.title)

        assertTrue(bookOne.key > 0)

        assertEquals("Marc J. Schiller", bookOne.author)
        assertEquals("Os 11 segredos de líderes de TI altamente influentes", bookOne.title)
        assertEquals(45.00, bookOne.price)
    }

    @Test
    @Order(6)
    fun `deve retornar acesso negado - FindAll sem token`() {
        var specificationWithoutToken = RequestSpecBuilder()
            .setPort(TestConfigs.SERVER_PORT)
            .setBasePath("/api/books/v1")
            .addFilter(RequestLoggingFilter(LogDetail.ALL))
            .addFilter(ResponseLoggingFilter(LogDetail.ALL))
            .build()

        RestAssured.given()
            .spec(specificationWithoutToken)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
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
    fun testHATEOAS() {
        val content = RestAssured.given()
            .spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .queryParams(
                "page", 0,
                "size",12,
                "direction", "asc")
            .`when`()
            .get()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString()

        assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8888/api/books/v1/12"}}}"""))
        assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8888/api/books/v1/3"}}}"""))
        assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8888/api/books/v1/4"}}}"""))
        assertTrue(content.contains("""_links":{"self":{"href":"http://localhost:8888/api/books/v1/10"}}}"""))

        assertTrue(content.contains("""{"first":{"href":"http://localhost:8888/api/books/v1?direction=asc&page=0&size=12&sort=title,asc"}"""))
        assertTrue(content.contains(""","self":{"href":"http://localhost:8888/api/books/v1?direction=asc&page=0&size=12&sort=title,asc"}"""))
        assertTrue(content.contains(""","next":{"href":"http://localhost:8888/api/books/v1?direction=asc&page=1&size=12&sort=title,asc"}"""))
        assertTrue(content.contains(""","last":{"href":"http://localhost:8888/api/books/v1?direction=asc&page=1&size=12&sort=title,asc"}}"""))

        assertTrue(content.contains(""""page":{"size":12,"totalElements":15,"totalPages":2,"number":0}}"""))
    }

}