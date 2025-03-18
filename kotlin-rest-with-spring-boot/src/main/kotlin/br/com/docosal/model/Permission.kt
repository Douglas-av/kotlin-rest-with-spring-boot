package br.com.docosal.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import java.lang.NullPointerException
import java.util.Date

@Entity
@Table(name = "permission")
class Permission : GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "description", length = 255)
    var description: String? = null

    override fun getAuthority(): String {
        return description!!
    }

}