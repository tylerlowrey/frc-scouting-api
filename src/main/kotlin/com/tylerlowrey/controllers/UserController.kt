package com.tylerlowrey.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.tylerlowrey.configuration.ApplicationConfiguration
import com.tylerlowrey.models.User
import com.tylerlowrey.repositories.UserRepository
import com.tylerlowrey.services.PasswordHashingService
import jakarta.ws.rs.core.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    val userRepository: UserRepository,
    val passwordHashingService: PasswordHashingService,
    val appConfig: ApplicationConfiguration,
) {
    @GetMapping("/users", produces = [MediaType.APPLICATION_JSON])
    fun getAllUsers(): GetAllUsersResponse {
        return GetAllUsersResponse(userRepository.findAll().toList())
    }

    @PostMapping("/users", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun createUser(@RequestBody request: UserCreationRequest): ResponseEntity<UserCreationResponse> {
        val existingUser = userRepository.findByUsername(request.newUser.username)
        if (existingUser == null || request.newUser.username.isEmpty()
            || request.newUser.username.length > appConfig.maxUsernameLength) {
            val createdUser = userRepository.save(User(
                username = request.newUser.username,
                passwordHash = passwordHashingService.hashPassword(request.newUser.password)
            ))
            return ResponseEntity.ok(UserCreationResponse(userId = createdUser.id, "User created"))
        }
        return ResponseEntity.badRequest().body(UserCreationResponse(-1, "Username is invalid"))
    }
}

data class GetAllUsersResponse(@JsonProperty("users") val users: List<User>)
data class NewUser(
    @JsonProperty("username") val username: String,
    @JsonProperty("password") val password: String
)
data class UserCreationRequest(@JsonProperty("newUser") val newUser: NewUser)
data class UserCreationResponse(
    @JsonProperty("userId") val userId: Long,
    @JsonProperty("msg") val msg: String
)