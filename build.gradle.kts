import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.0.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.spring") version "1.7.20"
    kotlin("plugin.jpa") version "1.7.20"
    application
}

group = "com.tylerlowrey"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    maven {
        url = uri("https://repo.spring.io/snapshot")
    }
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.platform:jakarta.jakartaee-api:9.1.0")

    implementation("org.springframework.boot:spring-boot-starter-web")
    //implementation("org.springframework.boot:spring-boot-starter-security")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register("runDev") {
    dependsOn("jar")

    doLast {
        val jarTask = tasks.getByName<BootJar>("bootJar")
        val jarName = jarTask.archiveFile.get().asFile.name
        exec {
            val environmentVariables = loadEnvironmentVariablesFromFile(".env.development.local")
            environmentVariables.map  {
                environment(it.key, it.value)
            }
            println("$buildDir/libs/$jarName")
            commandLine = listOf("java", "-jar", "$buildDir/libs/$jarName")
        }
    }
}

application {
    mainClass.set("com.tylerlowrey.FrcScoutingApiApplicationKt")
}

fun loadEnvironmentVariablesFromFile(fileName: String): MutableMap<String, String> {
    val environmentVariables = mutableMapOf<String, String>()
    file(fileName).readLines().forEach {
        val (key, value) = it.split("=")
        environmentVariables[key] = value
    }
    return environmentVariables
}