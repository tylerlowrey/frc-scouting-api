package com.tylerlowrey.services

import com.tylerlowrey.configuration.ApplicationConfiguration
import com.tylerlowrey.models.RefreshToken
import com.tylerlowrey.models.User
import com.tylerlowrey.repositories.RefreshTokenRepository
import io.mockk.*
import org.junit.jupiter.api.Test

class TokenServiceTest {
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

}