package br.com.docosal.services

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CepService (private val restTemplate: RestTemplate){
    private val url: String = "https://viacep.com.br/ws/"


    fun getCep(cep: String) : Boolean {
        return try {
            val headers = HttpHeaders()
            headers.set("Authorization", cep)
            val entity = HttpEntity<String>(headers)
            val response = restTemplate.exchange("${url}/${cep}/json", HttpMethod.GET, entity, HttpStatus::class.java)
            println("CEP ${cep} consultado: ${response.statusCode}")
            response.statusCode == HttpStatus.OK
        } catch (e: Exception) {
            println("Erro ao consultar o CEP: ${e.message}")
            false
        }
    }
}