package br.com.docosal.unittests.mocks

import br.com.docosal.data.vo.v1.BookDTO
import br.com.docosal.model.Book

class MockBook {
    fun mockEntity(): Book {
        return mockEntity(0)
    }

    fun mockEntity(number: Int): Book {
        val book = Book()
        book.author = "Author test $number"
        book.title = "Title test $number"
        book.id = number.toLong()
        book.price = 25.0
        return book
    }

    fun mockEntityList(): ArrayList<Book> {
        val books: ArrayList<Book> = ArrayList<Book>()
        for (i in 0..13) {
            books.add(mockEntity(i))
        }
        return books
    }

    fun mockDTO(): BookDTO{
        return mockDTO(0)
    }

    fun mockDTO(number: Int): BookDTO {
        val book = BookDTO()
        book.author = "Author test $number"
        book.title = "Title test $number"
        book.key = number.toLong()
        book.price = 25.0
        return book
    }

    fun mockDTOList (): ArrayList<BookDTO>{
        val books: ArrayList<BookDTO> = ArrayList()
        for (i in 0 .. 13){
            books.add(mockDTO(i))
        }
        return books
    }

}