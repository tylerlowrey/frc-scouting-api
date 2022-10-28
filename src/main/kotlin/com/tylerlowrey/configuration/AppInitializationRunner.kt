package com.tylerlowrey.configuration

import com.tylerlowrey.models.AccessToken
import com.tylerlowrey.models.User
import com.tylerlowrey.repositories.AccessTokenRepository
import com.tylerlowrey.repositories.UserRepository
import com.tylerlowrey.services.PasswordHashingService
import mu.KotlinLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import kotlin.reflect.full.memberProperties

private val logger = KotlinLogging.logger {}

@Component
class AppInitializationRunner(
    val appConfig: ApplicationConfiguration,
    val userRepository: UserRepository,
    val passwordHashingService: PasswordHashingService,
    val accessTokenRepository: AccessTokenRepository
): ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        logger.info { "App Environment: ${appConfig.appEnvironment}" }
        if (appConfig.appEnvironment == AppEnvironments.Development) {
            logger.info { "============ App Config Values ============" }
            for (configProperty in ApplicationConfiguration::class.memberProperties) {
                logger.info { "${configProperty.name} = ${configProperty.get(appConfig)}" }
            }
            logger.info { "===========================================" }

            addDevelopmentTestData()
        }
    }

    fun addDevelopmentTestData() {
        val createdUser = userRepository.save(User(
            username = "teejay",
            passwordHash = passwordHashingService.hashPassword("abc123")
        ))

        val twoWeeksInMilliseconds = 1000 * 60 * 60 * 24 * 7

        accessTokenRepository.save(AccessToken(
            id = "abc123",
            valid = true,
            expires = System.currentTimeMillis() + twoWeeksInMilliseconds,
            user = createdUser
        ))
    }
}