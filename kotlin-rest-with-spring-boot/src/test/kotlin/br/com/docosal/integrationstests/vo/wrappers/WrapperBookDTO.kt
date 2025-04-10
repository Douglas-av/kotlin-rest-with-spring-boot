package br.com.docosal.integrationstests.vo.wrappers

import br.com.docosal.integrationstests.vo.BookDTO
import com.fasterxml.jackson.annotation.JsonProperty

class WrapperBookDTO {

    @JsonProperty("_embedded")
    var embeded : BookEmbeded? = null

    @JsonProperty("content")
    var content : List<BookDTO>? = null
}