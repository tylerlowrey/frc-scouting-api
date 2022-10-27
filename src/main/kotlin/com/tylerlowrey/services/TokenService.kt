package com.tylerlowrey.services

import com.tylerlowrey.configuration.ApplicationConfiguration
import com.tylerlowrey.models.RefreshToken
import com.tylerlowrey.models.User
import com.tylerlowrey.repositories.AccessTokenRepository
import com.tylerlowrey.repositories.RefreshTokenRepository
import com.tylerlowrey.repositories.UserRepository
import jakarta.inject.Singleton
import org.springframework.stereotype.Component
import java.util.*

interface TokenService {
    fun createRefreshTokenForUser(userId: Long): String
    fun invalidateRefreshToken(token: String)
    fun getUsersRefreshTokens(userId: Long): List<RefreshToken>
    fun createAccessTokenForUser(userId: Long): String
    fun invalidateAccessToken(token: String)
    fun getUsersAccessTokens(userId: Long)
}

@Component
@Singleton
class TokenServiceImpl(
    val userRepository: UserRepository,
    val accessTokenRepository: AccessTokenRepository,
    val refreshTokenRepository: RefreshTokenRepository,
    val appConfig: ApplicationConfiguration
): TokenService {

    override fun createRefreshTokenForUser(userId: Long): String {
        val token = UUID.randomUUID().toString()
        refreshTokenRepository.save(RefreshToken(
            token = token,
            valid = true,
            expires = System.currentTimeMillis() + appConfig.refreshTokenExpirationLengthInMillis,
            user = User(id = userId)
        ))
        return token
    }

    override fun invalidateRefreshToken(token: String) {
        refreshTokenRepository.updatedRefreshTokenAsInvalid(token)
    }

    override fun getUsersRefreshTokens(userId: Long): List<RefreshToken> {
        return refreshTokenRepository.getRefreshTokensByUserId(userId)
    }

    override fun createAccessTokenForUser(userId: Long): String {
        TODO("Not yet implemented")
    }

    override fun invalidateAccessToken(token: String) {
        TODO("Not yet implemented")
    }

    override fun getUsersAccessTokens(userId: Long) {
        TODO("Not yet implemented")
    }

}