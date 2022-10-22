package com.tylerlowrey.configuration

import jakarta.inject.Inject
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration(proxyBeanMethods = false)
class CorsConfiguration
@Inject
constructor(val appConfig: ApplicationConfiguration) {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object: WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                println("Web App Origin Url: ${appConfig.webAppOriginUrl}")
                registry.addMapping("/**")
                    .allowedOrigins(appConfig.webAppOriginUrl)
            }
        }
    }
}