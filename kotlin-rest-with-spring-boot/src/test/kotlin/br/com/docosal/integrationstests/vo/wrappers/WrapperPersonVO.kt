package br.com.docosal.integrationstests.vo.wrappers

import br.com.docosal.integrationstests.vo.PersonVO
import com.fasterxml.jackson.annotation.JsonProperty

class WrapperPersonVO {

    @JsonProperty("_embedded")
    var embeded : PersonEmbeded? = null

    @JsonProperty("content")
    var content : List<PersonVO>? = null
}