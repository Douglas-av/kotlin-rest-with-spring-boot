package br.com.docosal.exceptions

import java.time.LocalDateTime

data class ExceptionResponse (
    val message: String?,
    val status: Int,
    val details: String = "",
    val timestamp: LocalDateTime = LocalDateTime.now(),

)