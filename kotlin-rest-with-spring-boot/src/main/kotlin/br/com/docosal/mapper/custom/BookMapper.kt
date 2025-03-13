package br.com.docosal.mapper.custom

import br.com.docosal.data.vo.v1.BookDTO
import br.com.docosal.model.Book
import org.springframework.stereotype.Service

@Service
class BookMapper {

    fun mapToBookEntity(bookDTO: BookDTO): Book {
        var entity : Book = Book()
        entity.id = bookDTO.key
        entity.author = bookDTO.author
        entity.launchDate = bookDTO.launchDate
        entity.title = bookDTO.title
        entity.price = bookDTO.price

        return entity
    }

    fun mapToBookDTO(book: Book): BookDTO {
        var bookDTO : BookDTO = BookDTO()
        bookDTO.key = book.id
        bookDTO.author = book.author
        bookDTO.launchDate = book.launchDate
        bookDTO.title = book.title
        bookDTO.price = book.price

        return bookDTO
    }

}