package br.com.docosal.services

import br.com.docosal.data.vo.v1.AccountCredentialsDTO
import br.com.docosal.data.vo.v1.TokenDTO
import br.com.docosal.repository.UserRepository
import br.com.docosal.security.jwt.JwtTokenProvider
import io.micrometer.observation.annotation.Observed
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.apache.logging.log4j.Logger
import org.slf4j.MDC
import java.time.OffsetDateTime

@Service
class AuthService {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var tokenProvider: JwtTokenProvider

    @Autowired
    lateinit var repository: UserRepository

    private val logger : Logger = LogManager.getLogger(BookService::class.java.name)

    @Observed(name = "user.login", contextualName = "login")
    fun signin(data: AccountCredentialsDTO) : ResponseEntity<*> {
        try {
            MDC.put("className", this::class.java.name)
            MDC.put("Time", OffsetDateTime.now().toString())
            logger.info("Trying log user ${data.username}")
        } finally {
            MDC.clear()
        }

        return try {
            val username = data.username
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