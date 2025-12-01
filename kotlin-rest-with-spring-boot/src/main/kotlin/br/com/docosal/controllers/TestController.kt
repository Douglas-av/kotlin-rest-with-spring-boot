package br.com.docosal.controllers

import br.com.docosal.util.MediaType
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/teste")
class TestController {


    private val logger = LoggerFactory.getLogger(TestController::class.java)

    @GetMapping(
        value = ["/testHeader"],
        produces = [MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML]
    )
    fun test(req: HttpServletRequest, res: HttpServletResponse): ResponseEntity<Any> {
        req.headerNames.iterator().forEachRemaining { logger.info("Header: ${it} - ${req.getHeader(it)}") }
        return ResponseEntity.ok("Sucesso")
    }

}