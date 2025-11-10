package br.com.docosal.services

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class MyInterceptor(
    // O AuthorizationService será injetado pelo Spring
    private val cepService: CepService
) : HandlerInterceptor  {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val httpRequest = request
        val httpResponse = response

        // Obtenha o token do header 'Authorization'
        val bearerToken = httpRequest.getHeader("Authorization")

        // Obtenha o client_id. Uma abordagem simples é usar um header customizado, por exemplo.
        // Se você precisar ler do corpo, a lógica aqui é mais complexa, pois requer a leitura do
        // stream de entrada da requisição sem consumi-lo para o próximo filtro ou controller.
        val clientId = httpRequest.getHeader("X-Client-Id")

        if (bearerToken != null) {
            val isAuthorized = cepService.getCep("01001000")
            if (isAuthorized) {
                // Se o serviço de autorização retornou true, o fluxo da requisição continua
                return true
            } else {
                // Caso contrário, retorna um erro HTTP 401 Unauthorized
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Não autorizado")
                return false
            }
        } else {
            // Se o token ou o client_id não estiverem presentes, a requisição é inválida
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token de autorização ou Client ID ausente")
            return false
        }
    }
}