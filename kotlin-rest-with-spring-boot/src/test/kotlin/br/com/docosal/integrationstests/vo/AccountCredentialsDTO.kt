package br.com.docosal.integrationstests.vo

import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement
data class AccountCredentialsDTO (

    var username: String? = null,

    var password: String? = null
)