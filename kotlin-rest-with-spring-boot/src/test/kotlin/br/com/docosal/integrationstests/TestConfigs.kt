package br.com.docosal.integrationstests

object TestConfigs {
    const val SERVER_PORT = 8888
    const val SERVER_URI = "http://localhost:$SERVER_PORT"

    const val CONTENT_TYPE_JSON = "application/json"
    const val CONTENT_TYPE_XML = "application/xml"
    const val CONTENT_TYPE_YML = "application/x-yml"

    const val HEADER_PARAM_ORIGIN = "Origin"
    const val HEADER_PARAM_AUTHORIZATION = "Authorization"

    const val ORIGIN_LOCAL_HOST = "http://localhost:8080"
    const val ORIGIN_GOOGLE = "https://google.com"

}