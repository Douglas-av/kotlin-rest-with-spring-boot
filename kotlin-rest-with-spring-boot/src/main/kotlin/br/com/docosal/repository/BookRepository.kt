package br.com.docosal.repository

import br.com.docosal.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<Book, Long> {

}