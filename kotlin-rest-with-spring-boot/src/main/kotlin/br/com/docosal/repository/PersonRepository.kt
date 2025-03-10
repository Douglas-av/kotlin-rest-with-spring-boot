package br.com.docosal.repository

import br.com.docosal.model.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository : JpaRepository<Person, Long> {
    fun findAllByFirstName(firstName: String): List<Person>;
}