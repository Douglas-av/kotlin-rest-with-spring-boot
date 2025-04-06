package br.com.docosal.integrationstests.vo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.github.dozermapper.core.Mapping
import org.springframework.hateoas.RepresentationModel
import java.util.*

@JsonPropertyOrder("id", "title", "author", "launch_date", "price")
data class BookDTO (

    @Mapping("id") // Serve para capturar corretamente o ID da tabela.
    @field:JsonProperty("id")
    var key: Long = 0,

    @field:JsonProperty("author")
    var author: String = "",

    @field:JsonProperty("launch_date")
    var launchDate: Date = Date(),

    @field:JsonProperty("price")
    var price: Double = 0.0,

    @field:JsonProperty("title")
    var title: String = ""

)