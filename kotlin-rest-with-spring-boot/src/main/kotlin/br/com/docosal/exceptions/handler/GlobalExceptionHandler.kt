package br.com.docosal.exceptions.handler

import br.com.docosal.exceptions.ExceptionResponse
import br.com.docosal.exceptions.InvalidJwtAuthenticationException
import br.com.docosal.exceptions.ResourceNotFoundException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionResponse>{
        val errorMessage = ex.bindingResult.allErrors.joinToString { it.defaultMessage?: "Erro de validacao" }
        val errorResponse = ExceptionResponse(status = HttpStatus.BAD_REQUEST.value(), message = "Erro de validacao: ${ex.message}", details = "Detalhes")
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(AccessDeniedException::class)
    fun handleUnauthorizedException(ex: AccessDeniedException): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse("Acesso não autorizado. ${ex.message}", HttpStatus.UNAUTHORIZED.value(),)
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleBadRequestException(ex: MethodArgumentTypeMismatchException): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse("Requisição inválida. ${ex.message}", HttpStatus.BAD_REQUEST.value() )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse("Recurso não encontrado. ${ex.message}", HttpStatus.NOT_FOUND.value() )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(ex: NoResourceFoundException): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse("Esta pagina nao e valida. ${ex.message}", HttpStatus.NOT_FOUND.value() )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ExceptionResponse> {
        val errorResponse = ExceptionResponse("Erro interno no servidor. ${ex.message}. ${ex.cause}", HttpStatus.INTERNAL_SERVER_ERROR.value() )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

//    @ExceptionHandler(InvalidJwtAuthenticationException::class)
//    fun handleInvalidJwtAuthenticationException(ex: Exception) : ResponseEntity<ExceptionResponse>{
//        val errorResponse = ExceptionResponse("", HttpStatus.FORBIDDEN.value())
//        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
//    }
//
//    @ExceptionHandler(JWTVerificationException::class)
//    fun handleInvalidJWTVerificationException(ex: Exception) : ResponseEntity<ExceptionResponse>{
//        val errorResponse = ExceptionResponse("GlobalHandle - Expired or invalid JWT Token! Confira! ${ex.message}", HttpStatus.FORBIDDEN.value())
//        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
//    }
}