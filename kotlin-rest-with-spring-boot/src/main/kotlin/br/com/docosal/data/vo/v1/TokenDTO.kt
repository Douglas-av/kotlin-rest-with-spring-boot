package br.com.docosal.data.vo.v1

import org.springframework.hateoas.RepresentationModel
import java.util.Date

data class TokenDTO (

    var userName: String? = null,
    var authenticated: Boolean? = null,
    var created: Date? = null,
    var expiration: Date? = null,
    var accessToken: String? = null,
    var refreshToken: String? = null
)