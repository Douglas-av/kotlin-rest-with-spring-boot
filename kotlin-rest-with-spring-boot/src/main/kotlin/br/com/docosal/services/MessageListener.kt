package br.com.docosal.services

import br.com.docosal.data.vo.v1.BookDTO
import br.com.docosal.data.vo.v1.PersonVO
import br.com.docosal.mapper.DozerMapper
import br.com.docosal.model.Book
import br.com.docosal.services.payload.BookPayload
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.annotation.SqsListener
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.sqs.model.Message

@Service
class MessageListener {

    @Autowired
    private lateinit var objectMapper: ObjectMapper
    private val logger : Logger = LogManager.getLogger(BookService::class.java.name)
    @Autowired
    private lateinit var bookService: BookService
    @Autowired
    private lateinit var personService: PersonService



    @SqsListener("\${cloud.aws.sqs.book-queue}")
    fun handleBook(payload: Message, acknowledgement: Acknowledgement){
        logger.info("Lendo mensagem da fila SQS payload ${payload.body()}")
//        acknowledgement.acknowledge().also { logger.info("Ack realizado!") }
        var bookPayload = objectMapper.readValue(payload.body(), BookPayload::class.java)
        var bookDTO = DozerMapper.parseObject(bookPayload, BookDTO::class.java)
        try {
            bookService.create(bookDTO).also { logger.info("Mensagem lida com sucesso!") }
            acknowledgement.acknowledge().also { logger.info("Ack realizado!") }
        } catch (exception: Exception) {
            logger.info("Erro ao ler mensagem da fila. ${exception.message}")
        }

    }

//    @SqsListener("\${cloud.aws.sqs.person-queue}")
//    fun handlePerson(payloadPerson: String, acknowledgement: Acknowledgement){
//        logger.info("Lendo mensagem da fila SQS payload ${payloadPerson}")
////        try {
////            personService.create(payloadPerson).also { logger.info("Mensagem lida com sucesso!") }
////            acknowledgement.acknowledge().also { logger.info("Ack realizado!") }
////        } catch (exception: Exception) {
////            logger.info("Erro ao ler mensagem da fila. ${exception.message}")
////        }
//    }
}