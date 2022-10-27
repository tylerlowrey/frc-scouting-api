package com.tylerlowrey.services

import jakarta.enterprise.context.ApplicationScoped
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

interface PasswordHashingService {
    fun hashPassword(password: String): String
    fun validatePassword(password: String, passwordHash: String): Boolean
}

@Component
@ApplicationScoped
class BCryptPasswordHashingService: PasswordHashingService {
    val passwordEncoder = BCryptPasswordEncoder()

    override fun hashPassword(password: String): String {
        return passwordEncoder.encode(password)
    }

    override fun validatePassword(password: String, passwordHash: String): Boolean {
        return passwordEncoder.matches(password, passwordHash)
    }
}