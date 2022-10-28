package com.tylerlowrey.services

import com.tylerlowrey.configuration.ApplicationConfiguration
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class Auth0JwtServiceTest {
    private val jwtService: JWTService = Auth0JwtService(ApplicationConfiguration(
        jwtSigningSecret = "INSECURE_SECRET"
    ))

    @Test
    fun `Correctly creates a JWT`() {
        val expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6ImFiYzEyMyJ9.4OCdZWJxlExD6dN0IBnV6dqp8KclI7yVXJ6sbwf4P4ZJFOOOycxFYroQ1Po6_b-i8tSmLpHnTnM10kXBuNA9xg"
        val jwt = jwtService.createJwt("abc123")

        assert(jwt == expectedJwt)
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
        val expectedJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpZCI6ImFiYzEyMyJ9.4OCdZWJxlExD6dN0IBnV6dqp8KclI7yVXJ6sbwf4P4ZJFOOOycxFYroQ1Po6_b-i8tSmLpHnTnM10kXBuNA9xg"
        val jwt = jwtService.createJwt("abc123")
        assert(jwt == expectedJwt)

        val accessTokenId = jwtService.verifyAndRetrieveTokenId(jwt)
        assert(accessTokenId == "abc123")
    }

    @Test
    fun `verifyAndRetrieveTokenId returns null when it fails to decode an invalid token`() {
        val invalidToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0.eyJjbGFpbSI6ImFiYzEyMyJ9.XzGwECLxzz0sSA7PvqPUCRyrLNgRMyGrLcmPj76-8svRAwd7cK8KtrOyh-rMnCaWyl3YRKRkNWN0ABC0b9Sl5g"

        val accessTokenId = jwtService.verifyAndRetrieveTokenId(invalidToken)
        assertFalse(accessTokenId == "abc123")
    }

}