package com.tylerlowrey

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container

class PostgresIntegrationTest {
    companion object {
        @Container
        private val postgresContainer = PostgreSQLContainer("postgres:latest")

        @JvmStatic
        @BeforeAll
        fun setupIntegrationTests(){
            Flyway.configure()
                .dataSource(postgresContainer.jdbcUrl, postgresContainer.username, postgresContainer.password)
                .load()
                .migrate()
        }
    }
}