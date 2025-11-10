package br.com.docosal.services

import br.com.docosal.data.vo.v1.BookDTO
import br.com.docosal.data.vo.v1.PersonVO
import br.com.docosal.mapper.DozerMapper
import br.com.docosal.model.Book
import br.com.docosal.model.Person
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MessageSender (private val sqsTemplate: SqsTemplate){

    @Value("\${cloud.aws.sqs.book-queue}")
    private var bookQueueName = ""

    @Value("\${cloud.aws.sqs.person-queue}")
    private var personQueueName = ""

    fun publishBook(bookDTO: BookDTO): BookDTO{
        val book: Book = DozerMapper.parseObject(bookDTO, Book::class.java)
        sqsTemplate.send { to -> to.queue(bookQueueName).payload(book) }
        return DozerMapper.parseObject(book, BookDTO::class.java)
    }

    fun publishPerson(personVO: PersonVO): PersonVO{
        val person: Person = DozerMapper.parseObject(personVO, Person::class.java)
        sqsTemplate.send { to -> to.queue(bookQueueName).payload(person) }
        return DozerMapper.parseObject(person, PersonVO::class.java)
    }

}