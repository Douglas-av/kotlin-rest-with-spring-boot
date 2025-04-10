package br.com.docosal.integrationstests.vo.wrappers

import br.com.docosal.integrationstests.vo.BookDTO
import com.fasterxml.jackson.annotation.JsonProperty

class BookEmbeded {

    @JsonProperty("bookDTOList")
    var books : List<BookDTO>? = null
}