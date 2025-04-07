package br.com.docosal.repository

import br.com.docosal.model.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, Long> {
    fun findAllByFirstName(firstName: String): List<Person>;

    @Modifying
    @Query("UPDATE Person p SET p.enabled = false WHERE p.id =:id")
    fun disablePerson(@Param("id")id : Long?)

}