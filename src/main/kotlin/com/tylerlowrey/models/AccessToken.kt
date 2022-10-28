package com.tylerlowrey.models

import jakarta.persistence.*

@Entity
data class AccessToken(
    @Id
    var id: String = "",
    var valid: Boolean,
    var expires: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    var user: User
)