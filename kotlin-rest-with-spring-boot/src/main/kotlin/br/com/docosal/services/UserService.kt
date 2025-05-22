package br.com.docosal.services

import br.com.docosal.controllers.BookController
import br.com.docosal.data.vo.v1.BookDTO
import br.com.docosal.exceptions.RequiredObjectIsNullException
import br.com.docosal.exceptions.ResourceNotFoundException
import br.com.docosal.mapper.DozerMapper
import br.com.docosal.mapper.custom.BookMapper
import br.com.docosal.model.Book
import br.com.docosal.repository.BookRepository
import br.com.docosal.repository.UserRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.apache.logging.log4j.Logger

@Service
class UserService(@field:Autowired var repository: UserRepository) : UserDetailsService {

//    @Autowired
//    lateinit var repository: UserRepository

    private val logger : Logger = LogManager.getLogger(UserService::class.java.name)

    fun findById(id : Long): BookDTO {
        logger.info("Finding a book! ID: $id")
        var book = repository.findById(id)
            .orElseThrow { ResourceNotFoundException("Recurso nao encontrado.") }
        var bookDTO: BookDTO = DozerMapper.parseObject(book, BookDTO::class.java)
        val withSelfRel = linkTo(BookController::class.java).slash(bookDTO.key).withSelfRel()
        bookDTO.add(withSelfRel)
        return bookDTO
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        logger.info("Finding one User by Username! Username: $username")
        val user = repository.findByUsername(username)
        return user ?: throw UsernameNotFoundException("Username $username not found! Validar") // operador elvis
    }

}