package br.com.docosal.data.vo.v2

import br.com.docosal.data.vo.v1.PersonVO
import com.github.dozermapper.core.Mapping
import org.springframework.hateoas.RepresentationModel
import java.util.Date

data class PersonVO (

    @Mapping("id")
    var key: Long = 0,

    var firstName: String = "",
    var lastName: String = "",
    var address: String = "",
    var gender: String = "",
    var birthday: Date? = null

) : RepresentationModel<PersonVO>()