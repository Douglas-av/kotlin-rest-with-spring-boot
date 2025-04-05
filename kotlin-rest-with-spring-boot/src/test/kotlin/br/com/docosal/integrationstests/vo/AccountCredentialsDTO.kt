package br.com.docosal.integrationstests.vo

import org.springframework.hateoas.RepresentationModel

data class AccountCredentialsDTO (

    var username: String? = null,

    var password: String? = null
)