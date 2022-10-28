package com.tylerlowrey.services

import com.tylerlowrey.configuration.ApplicationConfiguration
import com.tylerlowrey.models.AccessToken
import com.tylerlowrey.models.RefreshToken
import com.tylerlowrey.models.User
import com.tylerlowrey.repositories.AccessTokenRepository
import com.tylerlowrey.repositories.RefreshTokenRepository
import jakarta.inject.Singleton
import org.springframework.stereotype.Component
import java.util.*

interface TokenService {
    fun createRefreshTokenForUser(userId: Long): String
    fun invalidateRefreshToken(token: String)
    fun getUsersRefreshTokens(userId: Long): List<RefreshToken>
    fun createAccessTokenForUser(userId: Long): String
    fun invalidateAccessToken(tokenId: String)
    fun getUsersAccessTokens(userId: Long): List<AccessToken>
}

@Component
@Singleton
class TokenServiceImpl(
    val accessTokenRepository: AccessTokenRepository,
    val refreshTokenRepository: RefreshTokenRepository,
    val jwtService: JWTService,
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
        val tokenId = UUID.randomUUID().toString()
        val expirationTime = System.currentTimeMillis() + appConfig.accessTokenExpirationLengthInMillis
        accessTokenRepository.save(AccessToken(
            tokenId,
            valid = true,
            expires = expirationTime,
            user = User(
                id = userId
            )
        ))
        return jwtService.createJwt(tokenId, expirationTime)
    }

    override fun invalidateAccessToken(tokenId: String) {
        return accessTokenRepository.updateAccessTokenAsInvalid(tokenId)
    }

    override fun getUsersAccessTokens(userId: Long): List<AccessToken> {
        return accessTokenRepository.getAccessTokensByUserId(userId)
    }

}