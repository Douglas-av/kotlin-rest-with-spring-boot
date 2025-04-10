package br.com.docosal.unittests.mockito.services

import br.com.docosal.exceptions.RequiredObjectIsNullException
import br.com.docosal.repository.BookRepository
import br.com.docosal.services.BookService
import br.com.docosal.unittests.mocks.MockBook
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import java.util.*

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    private lateinit var inputObject: MockBook

    @InjectMocks
    private lateinit var service: BookService

    @Mock
    private lateinit var repository: BookRepository

    @BeforeEach
    fun setUpMock() {
        inputObject = MockBook()
        MockitoAnnotations.openMocks(this)
    }
/*
    @Test
    fun findAll() {
        val list = inputObject.mockEntityList()

        `when`(repository.findAll()).thenReturn(list)

        val books = service.findAll()

        assertNotNull(books)
        assertEquals(14, books.size)

        val bookOne = books[1]
        assertNotNull(bookOne)
        assertNotNull(bookOne.key)
        assertNotNull(bookOne.links)
        assertTrue(bookOne.links.toString().contains("</api/books/v1/1>;rel=\"self\""))
        assertEquals(1, bookOne.key)
        assertEquals("Title test 1", bookOne.title)
        assertEquals("Author test 1", bookOne.author)
        assertEquals(25.0, bookOne.price)

        val bookFour = books[4]
        assertNotNull(bookFour)
        assertNotNull(bookFour.key)
        assertNotNull(bookFour.links)
        assertTrue(bookFour.links.toString().contains("</api/books/v1/4>;rel=\"self\""))
        assertEquals(4, bookFour.key)
        assertEquals("Title test 4", bookFour.title)
        assertEquals("Author test 4", bookFour.author)
        assertEquals(25.0, bookFour.price)

        val bookSeven = books[7]
        assertNotNull(bookSeven)
        assertNotNull(bookSeven.key)
        assertNotNull(bookSeven.links)
        assertTrue(bookSeven.links.toString().contains("</api/books/v1/7>;rel=\"self\""))
        assertEquals(7, bookSeven.key)
        assertEquals("Title test 7", bookSeven.title)
        assertEquals("Author test 7", bookSeven.author)
        assertEquals(25.0, bookSeven.price)

    }

 */

    @Test
    fun findById() {
        val book = inputObject.mockEntity(1)
        book.id = 1
        `when`(repository.findById(1)).thenReturn(Optional.of(book))

        val result = service.findById(1)

        assertNotNull(result)
        assertNotNull(result.key)
        assertNotNull(result.links)
        assertTrue(result.links.toString().contains("</api/books/v1/1>;rel=\"self\""))
        assertEquals(1, result.key)
        assertEquals("Title test 1", result.title)
        assertEquals("Author test 1", result.author)
        assertEquals(25.0, result.price)

    }

    @Test
    fun create() {
        val entity = inputObject.mockEntity(1)

        val persisted = entity.copy()
        persisted.id = 1

        `when`(repository.findById(1)).thenReturn(Optional.of(entity))
        `when`(repository.save(entity)).thenReturn(persisted)

        val bookDTO = inputObject.mockDTO(1)
        val result = service.update(bookDTO)

        assertNotNull(result)
        assertNotNull(result.key)
        assertNotNull(result.links)
        assertTrue(result.links.toString().contains("</api/books/v1/1>;rel=\"self\""))
        assertEquals(1, result.key)
        assertEquals("Title test 1", result.title)
        assertEquals("Author test 1", result.author)
        assertEquals(25.0, result.price)
    }

    @Test
    fun createWithNullBook() {
        val exception : Exception = assertThrows(
            RequiredObjectIsNullException::class.java
        ) {service.create(null)}

        val excpectedMessage = "It is not allowed to persist a null object"
        val actualMessage = exception.message
        assertTrue(actualMessage!!.contains(excpectedMessage))
    }


    @MockitoSettings(strictness = Strictness.WARN)
    @Test
    fun update() {
        val entity = inputObject.mockEntity(1)

        val persisted = entity.copy()
        persisted.id = 1

        `when`(repository.save(entity)).thenReturn(persisted)

        val bookDTO = inputObject.mockDTO(1)
        val result = service.create(bookDTO)

        assertNotNull(result)
        assertNotNull(result.key)
        assertNotNull(result.links)
        assertTrue(result.links.toString().contains("</api/books/v1/1>;rel=\"self\""))
        assertEquals(1, result.key)
        assertEquals("Title test 1", result.title)
        assertEquals("Author test 1", result.author)
        assertEquals(25.0, result.price)
    }

    @Test
    fun updateWithNullBook() {
        val exception : Exception = assertThrows(
            RequiredObjectIsNullException::class.java
        ) {service.update(null)}

        val excpectedMessage = "It is not allowed to persist a null object"
        val actualMessage = exception.message
        assertTrue(actualMessage!!.contains(excpectedMessage))
    }

    @Test
    fun delete() {
        val entity = inputObject.mockEntity(1)
        `when`(repository.findById(1)).thenReturn(Optional.of(entity))
        service.delete(1)

    }
}