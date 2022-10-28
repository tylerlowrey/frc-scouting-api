package com.tylerlowrey.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tylerlowrey.configuration.ApplicationConfiguration
import jakarta.inject.Singleton
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.Instant

interface JWTService {
    /**
     * Create a JWT with a given id and expiration
     *
     * @param tokenId - The unique String id for the token
     * @param expirationEpochMillis - The timestamp at which the token expires in milliseconds from the epoch
     */
    fun createJwt(tokenId: String, expirationEpochMillis: Long): String

    /**
     * Validates the provided JWT String
     *
     * @param jwt - The JSON Web Token (JWT)
     * @return Boolean - Whether the provided JWT String is valid
     */
    fun isJwtValid(jwt: String): Boolean

    /**
     * Returns the id claim of the provided token
     *
     * @param jwt - The JSON Web Token (JWT)
     * @return String | null - The id claim of the token or null if the claim is not there or the jwt is invalid
     */
    fun verifyAndRetrieveTokenId(jwt: String): String?
}

private val logger = KotlinLogging.logger {}

@Component
@Singleton
class Auth0JwtService(private val appConfig: ApplicationConfiguration): JWTService {

    private val jwtVerifier = JWT.require(Algorithm.HMAC512(appConfig.jwtSigningSecret))
        .build()

    override fun createJwt(tokenId: String, expirationEpochMillis: Long): String {
        return JWT.create()
            .withClaim("id", tokenId)
            .withExpiresAt(Instant.ofEpochMilli(expirationEpochMillis))
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
