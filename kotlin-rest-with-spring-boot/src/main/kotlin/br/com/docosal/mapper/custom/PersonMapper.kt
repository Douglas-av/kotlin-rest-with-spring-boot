package br.com.docosal.mapper.custom

import br.com.docosal.data.vo.v2.PersonVO
import br.com.docosal.model.Person
import org.springframework.stereotype.Service
import java.util.Date

@Service
class PersonMapper {

    fun mapVOToEntity(personVO: PersonVO) : Person{
        var entity: Person = Person()
        entity.id = personVO.key
        entity.firstName = personVO.firstName
        entity.lastName = personVO.lastName
        entity.address = personVO.address
        entity.gender = personVO.gender
//        entity.birthday = personVO.birthday
        return entity
    }

    fun mapEntityToVO(person: Person) : PersonVO{
        var personVO: PersonVO = PersonVO()
        personVO.key = person.id
        personVO.firstName = person.firstName
        personVO.lastName = person.lastName
        personVO.address = person.address
        personVO.gender = person.gender
        personVO.birthday = Date() // Mock value
        return personVO
    }

}