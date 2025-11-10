package br.com.docosal.security.jwt

import com.auth0.jwt.exceptions.JWTVerificationException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class JwtTokenFilter(@field:Autowired private val tokenProvider: JwtTokenProvider) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val token = tokenProvider.resolveToke(httpRequest)
        try {
            if (!token.isNullOrBlank() && tokenProvider.validateToken(token)) {
                val auth = tokenProvider.getAuthentication(token)
                SecurityContextHolder.getContext().authentication = auth
            }
            chain!!.doFilter(request, response)
        } catch (exception: JWTVerificationException) {
            httpResponse.status = HttpServletResponse.SC_UNAUTHORIZED
            httpResponse.contentType = "application/json"
            val errorMessage = """
                {
                    "status": 401,
                    "error": "Unauthorized",
                    "message": "${exception.message}"
                }
            """.trimIndent()
            httpResponse.writer.write(errorMessage)
        }

    }
}