package br.com.docosal.services

import br.com.docosal.data.vo.v1.PersonVO
import br.com.docosal.exceptions.ResourceNotFoundException
import br.com.docosal.mapper.DozerMapper
import br.com.docosal.model.Person
import br.com.docosal.repository.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class PersonService {

    @Autowired
    private lateinit var repository: PersonRepository

    private val logger = Logger.getLogger(PersonService::class.java.name)

    fun findAll(): List<PersonVO>{
        logger.info("Finding all persons!")
        val persons = repository.findAll()
        return DozerMapper.parseListObjects(persons, PersonVO::class.java)
    }

    fun findById(id: Long): PersonVO{
        logger.info("Finding one person! ID: $id")
        var person = repository.findById(id)
            .orElseThrow {ResourceNotFoundException("No records found for this ID!")}
        return DozerMapper.parseObject(person, PersonVO::class.java)
    }

    fun create(person: PersonVO) : PersonVO {
        logger.info("Creating one person with name ${person.firstName}")
        var entity: Person =  DozerMapper.parseObject(person, Person::class.java)
        var personVO: PersonVO = DozerMapper.parseObject(repository.save(entity), PersonVO::class.java)
        return personVO
    }

    fun update(person: PersonVO): PersonVO {
        logger.info("Updating one person! ID: ${person.id}")

        val entity = repository.findById(person.id)
            .orElseThrow {ResourceNotFoundException("No records found for this ID!")}

        entity.firstName = person.firstName
        entity.lastName = person.lastName
        entity.address = person.address
        entity.gender = person.gender

        return DozerMapper.parseObject(repository.save(entity), PersonVO::class.java)
    }

    fun delete(id: Long) {
        logger.info("Deleting one person! ID: $id")
        val entity = repository.findById(id)
            .orElseThrow {ResourceNotFoundException("No records found for this ID!")}
        repository.delete(entity)
    }

    private fun mockPerson(i: Int): PersonVO {
        val person = PersonVO()
        person.firstName = "Person Name $i"
        person.lastName = "Last Name $i"
        person.address = "Sao Paulo - BR"
        person.address = "Male"
        return person
    }

}