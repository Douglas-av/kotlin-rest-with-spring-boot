package br.com.docosal.services.payload

import java.util.Date

data class BookPayload(
    var id: Long = 0,

    var author: String = "",

    var launchDate: Date = Date(),

    var price: Double = 0.0,

    var title: String = ""
)
