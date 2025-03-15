package br.com.docosal.services

import br.com.docosal.controllers.BookController
import br.com.docosal.data.vo.v1.BookDTO
import br.com.docosal.exceptions.RequiredObjectIsNullException
import br.com.docosal.exceptions.ResourceNotFoundException
import br.com.docosal.mapper.DozerMapper
import br.com.docosal.mapper.custom.BookMapper
import br.com.docosal.model.Book
import br.com.docosal.repository.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class BookService {

    @Autowired
    lateinit var repository: BookRepository

    @Autowired
    lateinit var mapper : BookMapper

    private val logger = Logger.getLogger(BookService::class.java.name)

    fun findAll(): List<BookDTO> {
        logger.info("Finding all books!")
        var books: List<Book> = repository.findAll()
        var booksDTO: ArrayList<BookDTO> = DozerMapper.parseListObjects(books, BookDTO::class.java)
//        for (book in books){
//            bookDTO.add(mapper.mapToBookDTO(book))
//        }
        for (bookDTO in booksDTO){
            val withSelfRel = linkTo(BookController::class.java).slash(bookDTO.key).withSelfRel()
            bookDTO.add(withSelfRel)
        }
        return booksDTO
    }

    fun findById(id : Long): BookDTO {
        logger.info("Finding a book! ID: $id")
        var book = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Recurso nao encontrado.") }
        var bookDTO: BookDTO = DozerMapper.parseObject(book, BookDTO::class.java)
        val withSelfRel = linkTo(BookController::class.java).slash(bookDTO.key).withSelfRel()
        bookDTO.add(withSelfRel)
        return bookDTO
    }

    fun create(bookDTO: BookDTO?): BookDTO{
        if (bookDTO == null) throw  RequiredObjectIsNullException()
        var book : Book = DozerMapper.parseObject(bookDTO, Book::class.java)
        var bookDTOCreated =  DozerMapper.parseObject(repository.save(book), BookDTO::class.java)
        val withSelfRel = linkTo(BookController::class.java).slash(bookDTOCreated.key).withSelfRel()
        bookDTOCreated.add(withSelfRel)
        return bookDTOCreated
    }

    fun update(bookDTO: BookDTO?): BookDTO{
        if (bookDTO == null) throw  RequiredObjectIsNullException()
        logger.info("Updating a book! ID: ${bookDTO.key}")
        var book : Book = repository.findById(bookDTO.key).
        orElseThrow { ResourceNotFoundException("Nao existe um livro com o ID: ${bookDTO.key}") }
        book.title = bookDTO.title
        book.author = bookDTO.author
        book.price = bookDTO.price
        book.launchDate = bookDTO.launchDate

        var bookDTOReg: BookDTO = DozerMapper.parseObject(repository.save(book), BookDTO::class.java)
        val withSelfRel = linkTo(BookController::class.java).slash(bookDTOReg.key).withSelfRel()
        bookDTOReg.add(withSelfRel)
        return bookDTOReg
    }

    fun delete(id: Long){
        logger.info("Deleting a book! ID: $id")
        var book : Book = repository.findById(id).
        orElseThrow { ResourceNotFoundException("Nao existe um livro com o ID: ${id}") }
        repository.delete(book)
    }

}