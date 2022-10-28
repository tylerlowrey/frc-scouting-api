package com.tylerlowrey.configuration

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
@ApplicationScoped
data class ApplicationConfiguration(
    // Use SpEL if you want to define default values
    @Value("#{environment['WEB_APP_ORIGIN_URL'] ?: ''}")
    val webAppOriginUrl: String = "",
    @Value("#{environment['MAX_USERNAME_LENGTH'] ?: 30}")
    val maxUsernameLength: Int = 30,
    @Value("#{environment['APP_ENVIRONMENT'] ?: 'Production'}")
    val appEnvironment: AppEnvironments = AppEnvironments.Production,
    // Default value of 2 weeks in milliseconds
    @Value("#{environment['REFRESH_TOKEN_EXPIRATION_IN_MILLIS'] ?: 1209600000}")
    val refreshTokenExpirationLengthInMillis: Long = 1209600000,
    @Value("#{environment['ACCESS_TOKEN_EXPIRATION_IN_MILLIS'] ?: 3600000}")
    val accessTokenExpirationLengthInMillis: Long = 3600000,
    @Value("#{environment['JWT_SIGNING_SECRET']}")
    val jwtSigningSecret: String
)

enum class AppEnvironments {
    Production,
    Development
}