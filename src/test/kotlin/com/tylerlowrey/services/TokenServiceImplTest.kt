package com.tylerlowrey.services

import com.tylerlowrey.configuration.ApplicationConfiguration
import com.tylerlowrey.models.AccessToken
import com.tylerlowrey.models.RefreshToken
import com.tylerlowrey.models.User
import com.tylerlowrey.repositories.AccessTokenRepository
import com.tylerlowrey.repositories.RefreshTokenRepository
import io.mockk.*
import org.junit.jupiter.api.Test

class TokenServiceImplTest {
    private val testAppConfig = ApplicationConfiguration(jwtSigningSecret = "INSECURE_SECRET")

    @Test
    fun `createRefreshTokenForUser returns a valid refresh token`() {
        val mockedRefreshTokenRepo = mockk<RefreshTokenRepository>()
        every { mockedRefreshTokenRepo.save(any()) } returns RefreshToken(user = User())
        val tokenService = TokenServiceImpl(
            accessTokenRepository = mockk(),
            refreshTokenRepository = mockedRefreshTokenRepo,
            jwtService = mockk(),
            appConfig = testAppConfig
        )
        val refreshToken = tokenService.createRefreshTokenForUser(1)

        assert(refreshToken != "")

        verify(exactly = 1) {
            mockedRefreshTokenRepo.save(match {
                it.valid && it.user.id == 1L && it.expires > System.currentTimeMillis()
            })
        }
        confirmVerified(mockedRefreshTokenRepo)
    }

    @Test
    fun `invalidateRefreshToken calls repository method to update refresh token as invalid`() {
        val mockedRefreshTokenRepo = mockk<RefreshTokenRepository>()
        every { mockedRefreshTokenRepo.updatedRefreshTokenAsInvalid("testToken") } returns Unit
        val tokenService = TokenServiceImpl(
            accessTokenRepository = mockk(),
            refreshTokenRepository = mockedRefreshTokenRepo,
            jwtService = mockk(),
            appConfig = testAppConfig
        )

        tokenService.invalidateRefreshToken("testToken")

        verify(exactly = 1) {
            mockedRefreshTokenRepo.updatedRefreshTokenAsInvalid("testToken")
        }
        confirmVerified(mockedRefreshTokenRepo)
    }

    @Test
    fun `getUsersRefreshTokens calls repository method to find all refresh tokens by user id`() {
        val mockedRefreshTokenRepo = mockk<RefreshTokenRepository>()
        every { mockedRefreshTokenRepo.getRefreshTokensByUserId(1L) } returns listOf(
            RefreshToken(user = User(id = 1L)),
            RefreshToken(user = User(id = 1L)),
            RefreshToken(user = User(id = 1L)),
        )
        val tokenService = TokenServiceImpl(
            accessTokenRepository = mockk(),
            refreshTokenRepository = mockedRefreshTokenRepo,
            jwtService = mockk(),
            appConfig = testAppConfig
        )

        val refreshTokens = tokenService.getUsersRefreshTokens(1L)
        assert(refreshTokens.size == 3)

        verify(exactly = 1) {
            mockedRefreshTokenRepo.getRefreshTokensByUserId(1L)
        }
        confirmVerified(mockedRefreshTokenRepo)
    }

    @Test
    fun `createAccessTokenForUser returns token from JWT Service and calls repository to save access token`() {
        val mockedAccessTokenRepo = mockk<AccessTokenRepository>()
        every {
            mockedAccessTokenRepo.save(any())
        } returns AccessToken(valid = true, expires = System.currentTimeMillis(), user = User())

        val mockedJwtService = mockk<JWTService>()
        every {
            mockedJwtService.createJwt(any(), any())
        } returns "testToken"

        val tokenService = TokenServiceImpl(
            accessTokenRepository = mockedAccessTokenRepo,
            refreshTokenRepository = mockk(),
            jwtService = mockedJwtService,
            appConfig = testAppConfig
        )

        val jwt = tokenService.createAccessTokenForUser(1L)
        assert(jwt == "testToken")

        verify(exactly = 1) {
            mockedAccessTokenRepo.save(match {
                it.valid && it.expires > System.currentTimeMillis() && it.user.id == 1L
            })
            mockedJwtService.createJwt(any(), any())
        }
        confirmVerified(mockedAccessTokenRepo)
        confirmVerified(mockedJwtService)
    }

    @Test
    fun `createAccessTokenForUser expiration time matches for call to JWT Service and call to repository`() {
        val mockedAccessTokenRepo = mockk<AccessTokenRepository>()
        every {
            mockedAccessTokenRepo.save(any())
        } returns AccessToken(valid = true, expires = System.currentTimeMillis(), user = User())

        val mockedJwtService = mockk<JWTService>()
        every {
            mockedJwtService.createJwt(any(), any())
        } returns "testToken"

        val tokenService = TokenServiceImpl(
            accessTokenRepository = mockedAccessTokenRepo,
            refreshTokenRepository = mockk(),
            jwtService = mockedJwtService,
            appConfig = testAppConfig
        )

        val jwt = tokenService.createAccessTokenForUser(1L)
        assert(jwt == "testToken")

        var expiresValueSavedByAccessTokenRepo: Long = 0
        verify(exactly = 1) {
            mockedAccessTokenRepo.save(match {
                expiresValueSavedByAccessTokenRepo = it.expires
                it.valid && it.expires > System.currentTimeMillis() && it.user.id == 1L
            })
        }

        verify(exactly = 1) {
            mockedJwtService.createJwt(any(), expiresValueSavedByAccessTokenRepo)
        }
        confirmVerified(mockedAccessTokenRepo)
        confirmVerified(mockedJwtService)
    }

    @Test
    fun `invalidateAccessToken calls repository function with correct parameters`() {
        val mockedAccessTokenRepo = mockk<AccessTokenRepository>(relaxed = true)
        val tokenService = TokenServiceImpl(
            accessTokenRepository = mockedAccessTokenRepo,
            refreshTokenRepository = mockk(),
            jwtService = mockk(),
            appConfig = testAppConfig
        )

        tokenService.invalidateAccessToken("testTokenId")

        verify(exactly = 1) {
            mockedAccessTokenRepo.updateAccessTokenAsInvalid("testTokenId")
        }
        confirmVerified(mockedAccessTokenRepo)
    }

    @Test
    fun `getUsersAccessTokens calls repository with correct parameters to return list of access tokens`() {
        val mockedAccessTokenRepo = mockk<AccessTokenRepository>()

        every { mockedAccessTokenRepo.getAccessTokensByUserId(any()) } returns listOf(
            AccessToken(valid = true, expires = System.currentTimeMillis() + 20000, user = User()),
            AccessToken(valid = true, expires = System.currentTimeMillis() + 99999, user = User()),
        )

        val tokenService = TokenServiceImpl(
            accessTokenRepository = mockedAccessTokenRepo,
            refreshTokenRepository = mockk(),
            jwtService = mockk(),
            appConfig = testAppConfig
        )

        val usersTokens = tokenService.getUsersAccessTokens(123L)
        assert(usersTokens.size == 2)

        verify(exactly = 1) {
            mockedAccessTokenRepo.getAccessTokensByUserId(123L)
        }
        confirmVerified(mockedAccessTokenRepo)
    }

}