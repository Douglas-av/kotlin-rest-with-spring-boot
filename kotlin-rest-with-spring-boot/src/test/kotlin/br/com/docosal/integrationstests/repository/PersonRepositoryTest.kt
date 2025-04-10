package br.com.docosal.integrationstests.repository

import br.com.docosal.integrationstests.testcontainers.AbstractIntegrationTest
import br.com.docosal.model.Person
import br.com.docosal.repository.PersonRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonRepositoryTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var repository: PersonRepository

    private lateinit var person : Person

    @BeforeAll
    fun setup(){
        person = Person()
    }

    @Test
    @Order(1)
    fun `GET deve retornar todas as pessoas que contem a string especifica`(){
        var pageable : Pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "firstName"))

        person = repository.findPersonByName("doug", pageable).content[0]

        assertNotNull(person.id)
        assertNotNull(person.firstName)
        assertNotNull(person.lastName)
        assertNotNull(person.address)
        assertNotNull(person.gender)

        assertTrue(person.id > 0)

        assertEquals("Douglas", person.firstName)
        assertEquals("Costa", person.lastName)
        assertEquals("Itaquaquecetuba - SP - Brasil", person.address)
        assertEquals("Male", person.gender)
        assertEquals(true, person.enabled)
    }

    @Test
    @Order(2)
    fun `GET deve desabilitar uma pessoa`(){
        repository.disablePerson(person.id)

        person = repository.findById(person.id).get()

        assertNotNull(person.id)
        assertNotNull(person.firstName)
        assertNotNull(person.lastName)
        assertNotNull(person.address)
        assertNotNull(person.gender)

        assertTrue(person.id > 0)

        assertEquals("Douglas", person.firstName)
        assertEquals("Costa", person.lastName)
        assertEquals("Itaquaquecetuba - SP - Brasil", person.address)
        assertEquals("Male", person.gender)
        assertEquals(false, person.enabled)
    }

}