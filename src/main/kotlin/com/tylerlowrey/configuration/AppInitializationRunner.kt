package com.tylerlowrey.configuration

import com.tylerlowrey.models.AccessToken
import com.tylerlowrey.models.User
import com.tylerlowrey.repositories.AccessTokenRepository
import com.tylerlowrey.repositories.UserRepository
import com.tylerlowrey.services.PasswordHashingService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import kotlin.reflect.full.memberProperties

@Component
class AppInitializationRunner(
    val appConfig: ApplicationConfiguration,
    val userRepository: UserRepository,
    val passwordHashingService: PasswordHashingService,
    val accessTokenRepository: AccessTokenRepository
): ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        println("App Environment: ${appConfig.appEnvironment}")
        if (appConfig.appEnvironment == AppEnvironments.Development) {
            println("\n")
            println("============ App Config Values ============")
            for (configProperty in ApplicationConfiguration::class.memberProperties) {
                println("${configProperty.name} = ${configProperty.get(appConfig)}")
            }
            println("\n")
        }
        val createdUser = userRepository.save(User(
            username = "teejay",
            passwordHash = passwordHashingService.hashPassword("abc123")
        ))

        val twoWeeksInMilliseconds = 1000 * 60 * 60 * 24 * 7

        accessTokenRepository.save(AccessToken(
            jwt = "abc123",
            valid = true,
            expires = System.currentTimeMillis() + twoWeeksInMilliseconds,
            user = createdUser
        ))
    }
}