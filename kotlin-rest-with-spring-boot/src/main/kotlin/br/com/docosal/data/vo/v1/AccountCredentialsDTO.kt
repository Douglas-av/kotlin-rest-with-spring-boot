package br.com.docosal.data.vo.v1

import org.springframework.hateoas.RepresentationModel

data class AccountCredentialsDTO (

    var username: String? = null,

    var password: String? = null
)