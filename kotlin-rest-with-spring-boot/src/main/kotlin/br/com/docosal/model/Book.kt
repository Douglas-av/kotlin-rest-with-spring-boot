package br.com.docosal.model

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "books")
data class Book (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 80)
    var author: String = "",

    @Column(name = "launch_date", nullable = false)
    var launchDate: Date = Date(),

    @Column(nullable = false)
    var price: Double = 0.0,

    @Column(nullable = false)
    var title: String = ""


)