package br.com.docosal.integrationstests.vo

import java.util.Date

data class TokenDTO (

    var username: String? = null,
    var authenticated: Boolean? = null,
    var created: Date? = null,
    var expiration: Date? = null,
    var accessToken: String? = null,
    var refreshToken: String? = null
)