//package br.com.docosal.integrationstests.sqs
//
//import br.com.docosal.integrationstests.TestConfigs
//import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
//import br.com.docosal.integrationstests.vo.BookDTO
//import com.fasterxml.jackson.databind.DeserializationFeature
//import com.fasterxml.jackson.databind.ObjectMapper
//import io.restassured.RestAssured
//import io.restassured.specification.RequestSpecification
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertNotNull
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.BeforeAll
//import org.junit.jupiter.api.MethodOrderer
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.TestInstance
//import org.junit.jupiter.api.TestMethodOrder
//import org.springframework.boot.test.context.SpringBootTest
//import java.util.Date
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class sqsTest : AbstractIntegrationTest(){
//
//    private lateinit var specification: RequestSpecification
//    private lateinit var objectMapper: ObjectMapper
//    private lateinit var bookDTO: BookDTO
//
//
//    @BeforeAll
//    fun setup() {
//        objectMapper = ObjectMapper()
//        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//        bookDTO = BookDTO()
//    }
//
//    @Test
//    fun `deve enviar mensagem para fila SQS`(){
//        mockBook()
//
//        val content = RestAssured.given()
//            .spec(specification)
//            .contentType(TestConfigs.CONTENT_TYPE_JSON)
//            .body(bookDTO)
//            .`when`()
//            .post()
//            .then()
//            .statusCode(200)
//            .extract()
//            .body()
//            .asString()
//
//        val createdBook = objectMapper.readValue(content, BookDTO::class.java)
//
//        assertNotNull(createdBook.author)
//        assertNotNull(createdBook.title)
//        assertNotNull(createdBook.launchDate)
//        assertNotNull(createdBook.price)
//
//        assertTrue(createdBook.key > 0)
//
//        assertEquals("Machado de Assis", createdBook.author)
//        assertEquals("Memorias postumas de brás cubas", createdBook.title)
//        assertEquals(20.00, createdBook.price)
//    }
//
//    private fun mockBook() {
//        bookDTO.author = "Machado de Assis"
//        bookDTO.title = "Memorias postumas de brás cubas"
//        bookDTO.launchDate = Date()
//        bookDTO.price = 20.00
//    }
//}