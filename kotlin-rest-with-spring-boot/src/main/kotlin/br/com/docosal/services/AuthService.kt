package br.com.docosal.services

import br.com.docosal.data.vo.v1.AccountCredentialsDTO
import br.com.docosal.data.vo.v1.TokenDTO
import br.com.docosal.repository.UserRepository
import br.com.docosal.security.jwt.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class AuthService {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var tokenProvider: JwtTokenProvider

    @Autowired
    lateinit var repository: UserRepository

    private val logger = Logger.getLogger(BookService::class.java.name)

    fun signin(data: AccountCredentialsDTO) : ResponseEntity<*> {
        logger.info("Trying log user ${data.userName}")
        return try {
            val username = data.userName
            val password = data.password
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
            val user = repository.findByUsername(username)
            val tokenResponse : TokenDTO = if (user != null) {
                tokenProvider.createAccessToken(username!!, user.roles)
            } else {
                throw UsernameNotFoundException("Username $username not found!")
            }
            ResponseEntity.ok(tokenResponse)
        } catch (ex : AuthenticationException) {
            throw BadCredentialsException("Invalid username or password supplied! Verifique.")
        }
    }

    fun refreshToken(username: String, refreshToken: String) : ResponseEntity<*> {
        logger.info("Trying get refresh token to user ${username}")

        val user = repository.findByUsername(username)
        val tokenResponse : TokenDTO = if (user != null) {
            tokenProvider.refreshToken(refreshToken)
        } else {
            throw UsernameNotFoundException("Username $username not found!")
        }
        return ResponseEntity.ok(tokenResponse)
    }
}