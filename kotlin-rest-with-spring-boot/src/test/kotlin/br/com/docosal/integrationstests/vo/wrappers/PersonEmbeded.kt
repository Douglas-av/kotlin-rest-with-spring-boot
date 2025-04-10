package br.com.docosal.integrationstests.vo.wrappers

import br.com.docosal.integrationstests.vo.PersonVO
import com.fasterxml.jackson.annotation.JsonProperty

class PersonEmbeded {

    @JsonProperty("personVOList")
    var persons : List<PersonVO>? = null
}