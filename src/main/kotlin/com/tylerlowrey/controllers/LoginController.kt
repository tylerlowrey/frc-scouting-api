package com.tylerlowrey.controllers

import jakarta.ws.rs.core.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController {
    @PostMapping( "/login", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON] )
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        if (loginRequest.username == "teejay" && loginRequest.password == "abc123") {
            return ResponseEntity.ok(LoginResponse("loginToken"))
        }

        return ResponseEntity.status(406).build()
    }
}

data class LoginResponse(var token: String?)

data class LoginRequest(val username: String, val password: String)