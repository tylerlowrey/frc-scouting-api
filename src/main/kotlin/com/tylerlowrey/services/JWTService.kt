package com.tylerlowrey.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tylerlowrey.configuration.ApplicationConfiguration
import jakarta.inject.Singleton
import mu.KotlinLogging
import org.springframework.stereotype.Component

interface JWTService {
    fun createJwt(tokenId: String): String
    fun isJwtValid(jwt: String): Boolean
    fun verifyAndRetrieveTokenId(jwt: String): String?
}

private val logger = KotlinLogging.logger {}

@Component
@Singleton
class Auth0JwtService(private val appConfig: ApplicationConfiguration): JWTService {

    private val jwtVerifier = JWT.require(Algorithm.HMAC512(appConfig.jwtSigningSecret))
        .build()

    override fun createJwt(tokenId: String): String {
        return JWT.create()
            .withClaim("id", tokenId)
            .sign(Algorithm.HMAC512(appConfig.jwtSigningSecret))
    }

    override fun isJwtValid(jwt: String): Boolean {
        return try { jwtVerifier.verify(jwt) != null } catch(e: Exception) {
            logger.error { e.message }
            false
        }
    }

    override fun verifyAndRetrieveTokenId(jwt: String): String? {
        val decodedToken = try { jwtVerifier.verify(jwt) } catch (e: Exception) { return null }
        return decodedToken.claims["id"]?.asString()
    }

}
