package com.tylerlowrey.controllers

import com.tylerlowrey.repositories.UserRepository
import com.tylerlowrey.services.PasswordHashingService
import jakarta.ws.rs.core.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController(val usersRepository: UserRepository, val passwordHashingService: PasswordHashingService) {
    @PostMapping( "/login", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON] )
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val user = usersRepository.findByUsername(loginRequest.username)
        if (user != null && passwordHashingService.validatePassword(loginRequest.password, user.passwordHash)) {
            return ResponseEntity.ok(LoginResponse("loginToken"))
        }

        return ResponseEntity.status(401).build()
    }
}

data class LoginResponse(var token: String?)
data class LoginRequest(val username: String, val password: String)