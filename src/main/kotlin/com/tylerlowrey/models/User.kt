package com.tylerlowrey.models

import jakarta.persistence.*

@Entity
@Table(name = "USERS") // Necessary to avoid user naming conflict with H2
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1,
    var username: String = "",
    var passwordHash: String = ""
)