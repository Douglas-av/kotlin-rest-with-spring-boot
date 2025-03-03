package br.com.docosal.services

import br.com.docosal.model.Person
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Logger

@Service
class PersonService {

    private val counter: AtomicLong = AtomicLong()

    private val logger = Logger.getLogger(PersonService::class.java.name)

    fun findAll(): List<Person>{
        logger.info("Finding all persons!")

        var persons: MutableList<Person> = ArrayList()
        for (i in 0..7){
            val person = mockPerson(i)
            persons.add(person)
        }

        return persons
    }

    fun findById(id: Long): Person{
        logger.info("Finding one person! ID: $id")

        val person = Person()
        person.id = counter.incrementAndGet()
        person.firstName = "Douglas"
        person.lastName = "Alves"
        person.address = "Sao Paulo - BR"
        person.address = "Male"
        return person
    }

    fun create(person: Person) = person

    fun update(person: Person) = person

    fun delete(id: Long) {}

    private fun mockPerson(i: Int): Person {
        val person = Person()
        person.id = counter.incrementAndGet()
        person.firstName = "Person Name $i"
        person.lastName = "Last Name $i"
        person.address = "Sao Paulo - BR"
        person.address = "Male"
        return person
    }

}