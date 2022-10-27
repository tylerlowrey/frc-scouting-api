package com.tylerlowrey.models

import jakarta.persistence.*

@Entity
data class AccessToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = -1,
    var jwt: String,
    var valid: Boolean,
    var expires: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    var user: User
)