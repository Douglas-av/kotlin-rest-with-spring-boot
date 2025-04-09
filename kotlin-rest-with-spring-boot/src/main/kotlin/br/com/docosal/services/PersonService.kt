package br.com.docosal.services

import br.com.docosal.controllers.PersonController
import br.com.docosal.data.vo.v1.PersonVO
import br.com.docosal.exceptions.RequiredObjectIsNullException
import br.com.docosal.data.vo.v2.PersonVO as PersonVOV2
import br.com.docosal.exceptions.ResourceNotFoundException
import br.com.docosal.mapper.DozerMapper
import br.com.docosal.mapper.custom.PersonMapper
import br.com.docosal.model.Person
import br.com.docosal.repository.PersonRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.logging.Logger

@Service
class PersonService {

    @Autowired
    private lateinit var repository: PersonRepository

    @Autowired
    private lateinit var assembler : PagedResourcesAssembler<PersonVO>

    @Autowired
    private lateinit var mapper: PersonMapper

    private val logger = Logger.getLogger(PersonService::class.java.name)

    fun findAll(pageable: Pageable): PagedModel<EntityModel<PersonVO>>{
        logger.info("Finding all persons!")
        val persons = repository.findAll(pageable)
        var personsVOs = persons.map { p -> DozerMapper.parseObject(p, PersonVO::class.java) }
        personsVOs.map { p -> p.add(linkTo(PersonController::class.java).slash(p.key).withSelfRel()) }
//        var personsVOs: ArrayList<PersonVO> = DozerMapper.parseListObjects(persons, PersonVO::class.java)
//        for (person in personsVOs){
//            val withSelfRel = linkTo(PersonController::class.java).slash(person.key).withSelfRel()
//            person.add(withSelfRel)
//        }
        return assembler.toModel(personsVOs)
    }

    fun findPersonByName(firstName: String, pageable: Pageable): PagedModel<EntityModel<PersonVO>> {

        logger.info("Finding all persons that contains in FirstName: $firstName!")

        val persons = repository.findPersonByName(firstName, pageable)
        val vos = persons.map { p -> DozerMapper.parseObject(p, PersonVO::class.java) }
        vos.map { p ->  p.add(linkTo(PersonController::class.java).slash(p.key).withSelfRel())}
        return assembler.toModel(vos)
    }

    fun findAllByFirstName(firstName: String): List<PersonVO>{
        logger.info("Finding all persons by FirstName: $firstName!")
        val persons = repository.findAllByFirstName(firstName)
        var personsVOs: ArrayList<PersonVO> = DozerMapper.parseListObjects(persons, PersonVO::class.java)
        for (person in personsVOs){
            val withSelfRel = linkTo(PersonController::class.java).slash(person.key).withSelfRel()
            person.add(withSelfRel)
        }
        return personsVOs
    }

    fun findById(id: Long): PersonVO{
        logger.info("Finding one person! ID: $id")
        var person = repository.findById(id)
            .orElseThrow {ResourceNotFoundException("No records found for this ID!")}
        val personVO: PersonVO = DozerMapper.parseObject(person, PersonVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO
    }

    fun create(person: PersonVO?) : PersonVO {
        if (person == null) throw  RequiredObjectIsNullException()
        logger.info("Creating one person with name ${person.firstName}")
        var entity: Person =  DozerMapper.parseObject(person, Person::class.java)
        var personVO: PersonVO = DozerMapper.parseObject(repository.save(entity), PersonVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO
    }

    fun createV2(person: PersonVOV2) : PersonVOV2 {
        logger.info("Creating one person with name ${person.firstName}")
        var entity: Person =  mapper.mapVOToEntity(person)
        var personVOV2: PersonVOV2 = mapper.mapEntityToVO(repository.save(entity))
        val withSelfRel = linkTo(PersonController::class.java).slash(personVOV2.key).withSelfRel()
        personVOV2.add(withSelfRel)
        return personVOV2
    }

    fun update(person: PersonVO?): PersonVO {
        if (person == null) throw  RequiredObjectIsNullException()
        logger.info("Updating one person! ID: ${person.key}")

        val entity = repository.findById(person.key)
            .orElseThrow {ResourceNotFoundException("No records found for this ID!")}

        entity.firstName = person.firstName
        entity.lastName = person.lastName
        entity.address = person.address
        entity.gender = person.gender

        var personVO: PersonVO = DozerMapper.parseObject(repository.save(entity), PersonVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO
    }

    @Transactional
    fun disablePerson(id: Long): PersonVO{
        logger.info("Disabling a person! Person id: $id")
        repository.disablePerson(id)

        var person = repository.findById(id)
            .orElseThrow {ResourceNotFoundException("No records found for this ID! $id")}
        val personVO: PersonVO = DozerMapper.parseObject(person, PersonVO::class.java)
        val withSelfRel = linkTo(PersonController::class.java).slash(personVO.key).withSelfRel()
        personVO.add(withSelfRel)
        return personVO
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