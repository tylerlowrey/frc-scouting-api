package com.tylerlowrey.configuration

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component


@Component
@ApplicationScoped
class ApplicationConfiguration
@Inject
constructor(environment: Environment) {
    val webAppOriginUrl = environment.getProperty("WEB_APP_ORIGIN_URL", "")
}