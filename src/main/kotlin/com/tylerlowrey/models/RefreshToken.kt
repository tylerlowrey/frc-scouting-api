package com.tylerlowrey.models

import jakarta.persistence.*

@Entity
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1,
    var token: String = "",
    var valid: Boolean = true,
    var expires: Long = System.currentTimeMillis(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    var user: User
)