package br.com.docosal.integrationstests.vo

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement
data class PersonVO (

    var id: Long = 0,

    @field:JsonProperty("first_name")
    var firstName: String = "",

    @field:JsonProperty("last_name")
    var lastName: String = "",
    var address: String = "",

    var gender: String = "",
    var enabled: Boolean = true

)