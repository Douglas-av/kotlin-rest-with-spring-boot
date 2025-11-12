package br.com.docosal.integrationstests.sqs

import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.integrationstests.vo.BookDTO
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class sqsTest : AbstractIntegrationTest() {

    private lateinit var objectMapper: ObjectMapper
    private lateinit var bookDTO: BookDTO

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        bookDTO = BookDTO()
    }

    @Test
    fun `deve enviar mensagem para fila SQS`(){
        mockBook()
        // Aqui você adicionaria a lógica para enviar a mensagem
        // e verificar se ela foi recebida.
    }

    private fun mockBook() {
        bookDTO.author = "Machado de Assis"
        bookDTO.title = "Memorias postumas de brás cubas"
        bookDTO.launchDate = Date()
        bookDTO.price = 20.00
    }
}
