package br.com.docosal.controllers

import br.com.docosal.data.vo.v1.PersonVO
import br.com.docosal.data.vo.v2.PersonVO as PersonVOV2
import br.com.docosal.services.PersonService
import br.com.docosal.util.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/person/v1")
class PersonController {

    @Autowired
    private lateinit var service: PersonService
//  var service: PersonService = PersonService()

    @GetMapping(produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    fun findAll(): List<PersonVO>{
        return service.findAll()
    }

    @GetMapping(value=["/firstName/{firstName}"], produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    fun findAllByFirstName(@PathVariable("firstName") firstName: String): List<PersonVO>{
        return service.findAllByFirstName(firstName)
    }

    @GetMapping(value=["/{id}"], produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    fun findById(@PathVariable(value="id") id: Long): PersonVO{
        return service.findById(id)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML],
        produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    fun create(@RequestBody personVO: PersonVO): PersonVO{
        return service.create(personVO)
    }

    @PostMapping(value=["/v2"],consumes = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    fun createV2(@RequestBody personVOV2: PersonVOV2): PersonVOV2{
        return service.createV2(personVOV2)
    }

    @PutMapping(consumes = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    fun update(@RequestBody personVO: PersonVO): PersonVO{
        return service.update(personVO)
    }

    @DeleteMapping(value=["/{id}"], produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML])
    fun delete(@PathVariable(value="id") id: Long): ResponseEntity<*>{
        service.delete(id)
        return ResponseEntity.noContent().build<Any>()

    }

}