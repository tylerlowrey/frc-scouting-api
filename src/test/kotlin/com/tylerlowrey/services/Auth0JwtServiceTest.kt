package com.tylerlowrey.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.tylerlowrey.configuration.ApplicationConfiguration
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant

class Auth0JwtServiceTest {
    private val jwtService: JWTService = Auth0JwtService(ApplicationConfiguration(
        jwtSigningSecret = "INSECURE_SECRET"
    ))

    @Test
    fun `createJwt creates a valid JWT`() {
        val expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6ImFiYzEyMyIsImV4cCI6MTUwfQ.jgWYyWpN4kZj5h8wnnNk9ww_J96bOYur98Rb2hleUVdfwfVncWuyDVczWXAAGIV32nqTYuTxVL0ZjRQGciG-lw"
        val jwt = jwtService.createJwt("abc123", Instant.EPOCH.toEpochMilli() + 150000)

        assert(jwt == expectedJwt)
    }

    @Test
    fun `Creates JWT with correct expiration time`() {
        val jwt = jwtService.createJwt("abc123", Instant.parse("2200-01-01T00:00:00.00Z").toEpochMilli())
        val jwtVerifier = JWT.require(Algorithm.HMAC512("INSECURE_SECRET")).build()

        val decodedToken = jwtVerifier.verify(jwt)
        assert(decodedToken.expiresAtAsInstant == Instant.parse("2200-01-01T00:00:00.00Z"))
    }

    @Test
    fun `Created JWT has correct claims and correct number of claims`() {
        val jwt = jwtService.createJwt("abc123", Instant.parse("2200-01-01T00:00:00.00Z").toEpochMilli())
        val jwtVerifier = JWT.require(Algorithm.HMAC512("INSECURE_SECRET")).build()

        val decodedToken = jwtVerifier.verify(jwt)
        assert(!decodedToken.getClaim("exp").isNull && !decodedToken.getClaim("exp").isMissing)
        assert(!decodedToken.getClaim("id").isNull && !decodedToken.getClaim("id").isMissing)
        assert(decodedToken.claims.size == 2)
    }

    @Test
    fun `Correctly validates a known valid JWT`() {
        val validJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJjbGFpbSI6ImFiYzEyMyJ9.XzGwECLxzz0sSA7PvqPUCRyrLNgRMyGrLcmPj76-8svRAwd7cK8KtrOyh-rMnCaWyl3YRKRkNWN0ABC0b9Sl5g"
        assert(jwtService.isJwtValid(validJwt))
    }

    @Test
    fun `JWT Validation fails if the none algorithm is specified in the token`() {
        val invalidJwtTokenWithNoneAlgorithm = "eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0.eyJjbGFpbSI6ImFiYzEyMyJ9.XzGwECLxzz0sSA7PvqPUCRyrLNgRMyGrLcmPj76-8svRAwd7cK8KtrOyh-rMnCaWyl3YRKRkNWN0ABC0b9Sl5g"
        assert(!jwtService.isJwtValid(invalidJwtTokenWithNoneAlgorithm))
    }

    @Test
    fun `Decodes a token and returns the access token id`() {
        val expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6ImFiYzEyMyIsImV4cCI6NzI1ODExODQwMH0.oHKwnCAu3O1Bj1yIGHI5K-wth3MShWu-J-kPQjiDGJ6D5wV_zoKuxCpQnvzlZFmiq7O6cPqxl51TcBlG2h--PQ"
        val jwt = jwtService.createJwt("abc123", Instant.parse("2200-01-01T00:00:00.00Z").toEpochMilli())
        assert(jwt == expectedJwt)

        val accessTokenId = jwtService.verifyAndRetrieveTokenId(jwt)
        assert(accessTokenId == "abc123")
    }

    @Test
    fun `verifyAndRetrieveTokenId returns null when it fails to decode an invalid token`() {
        val invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0.eyJjbGFpbSI6ImFiYzEyMyJ9.XzGwECLxzz0sSA7PvqPUCRyrLNgRMyGrLcmPj76-8svRAwd7cK8KtrOyh-rMnCaWyl3YRKRkNWN0ABC0b9Sl5g"

        val accessTokenId = jwtService.verifyAndRetrieveTokenId(invalidToken)
        assertNull(accessTokenId)
    }

    @Test
    fun `verifyAndRetrieveTokenId returns null when token is expired`() {
        val expiredToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6ImFiYzEyMyIsImV4cCI6MTUwfQ.jgWYyWpN4kZj5h8wnnNk9ww_J96bOYur98Rb2hleUVdfwfVncWuyDVczWXAAGIV32nqTYuTxVL0ZjRQGciG-lw"

        val accessTokenId = jwtService.verifyAndRetrieveTokenId(expiredToken)
        assertNull(accessTokenId)
    }

    @Test
    fun `verifyAndRetrieveTokenId returns null when id claim is missing`() {
        val jwtWithoutIdClaim = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJleHAiOjcyNTgxMTg0MDB9.CzPPTU1qXk1kNIe52ae9BanuFlZXUrv6p7jnMMxNK2CrH-ppGmfQUKDjhWhXEcAS76LxSclKB9HVH-EJqDS78Q"

        val accessTokenId = jwtService.verifyAndRetrieveTokenId(jwtWithoutIdClaim)
        assertNull(accessTokenId)
    }

}