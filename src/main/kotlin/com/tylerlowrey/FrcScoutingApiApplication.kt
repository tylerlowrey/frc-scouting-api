package com.tylerlowrey

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ConfigurationPropertiesScan("com.tylerlowrey")
class FrcScoutingApiApplication

fun main(args: Array<String>) {
    runApplication<FrcScoutingApiApplication>(*args)
}