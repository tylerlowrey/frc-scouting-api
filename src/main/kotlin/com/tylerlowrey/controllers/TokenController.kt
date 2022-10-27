package com.tylerlowrey.controllers

import com.tylerlowrey.models.AccessToken
import com.tylerlowrey.models.RefreshToken
import com.tylerlowrey.repositories.AccessTokenRepository
import com.tylerlowrey.repositories.RefreshTokenRepository
import jakarta.ws.rs.core.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenController(
    val accessTokenRepository: AccessTokenRepository,
    val refreshTokenRepository: RefreshTokenRepository
) {
    @GetMapping("/accessTokens", produces = [MediaType.APPLICATION_JSON])
    fun getAccessTokens(): List<AccessToken> {
        return accessTokenRepository.findAll().toList()
    }

    @GetMapping("/refreshTokens", produces = [MediaType.APPLICATION_JSON])
    fun getRefreshTokens(): List<RefreshToken> {
        return refreshTokenRepository.findAll().toList()
    }
}