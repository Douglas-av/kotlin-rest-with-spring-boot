package br.com.docosal.data.vo.v1

import org.springframework.hateoas.RepresentationModel

data class AccountCredentialsDTO (

    var userName: String? = null,

    var password: String? = null
)