package br.com.docosal.services

import br.com.docosal.data.vo.v1.BookDTO
import br.com.docosal.data.vo.v1.PersonVO
import io.awspring.cloud.sqs.annotation.SqsListener
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessageListener {

    private val logger : Logger = LogManager.getLogger(BookService::class.java.name)

    @Autowired
    private lateinit var bookService: BookService
    private lateinit var personService: PersonService

    @SqsListener("\${cloud.aws.sqs.book-queue}")
    fun handleBook(payloadBookDTO: BookDTO?, acknowledgement: Acknowledgement){
        logger.info("Lendo mensagem da fila SQS payload ${payloadBookDTO}")
        try {
            bookService.create(payloadBookDTO).also { logger.info("Mensagem lida com sucesso!") }
            acknowledgement.acknowledge().also { logger.info("Ack realizado!") }
        } catch (exception: Exception) {
            logger.info("Erro ao ler mensagem da fila. ${exception.message}")
        }

    }

    @SqsListener("\${cloud.aws.sqs.person-queue}")
    fun handlePerson(payloadPerson: PersonVO?, acknowledgement: Acknowledgement){
        logger.info("Lendo mensagem da fila SQS payload ${payloadPerson}")
        try {
            personService.create(payloadPerson).also { logger.info("Mensagem lida com sucesso!") }
            acknowledgement.acknowledge().also { logger.info("Ack realizado!") }
        } catch (exception: Exception) {
            logger.info("Erro ao ler mensagem da fila. ${exception.message}")
        }
    }
}