import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.0.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.spring") version "1.7.20"
    kotlin("plugin.jpa") version "1.7.20"
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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("com.ninja-squad:springmockk:3.1.1")}

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
    dependsOn("clean")
    dependsOn("bootJar").shouldRunAfter("clean")
    doLast {
        runWithEnvironment(".env.development.local")
    }
}

tasks.register("run") {
    dependsOn("clean")
    dependsOn("bootJar").shouldRunAfter("clean")
    doLast {
        runWithEnvironment(".env")
    }
}

fun runWithEnvironment(dotEnvFileName: String) {
    val jarTask = tasks.getByName<BootJar>("bootJar")
    val jarName = jarTask.archiveFile.get().asFile.name
    exec {
        val environmentVariables = loadEnvironmentVariablesFromFile(dotEnvFileName)
        environmentVariables.map  {
            environment(it.key, it.value)
        }
        commandLine = listOf("java", "-jar", "$buildDir/libs/$jarName")
    }
}

fun loadEnvironmentVariablesFromFile(fileName: String): MutableMap<String, String> {
    val environmentVariables = mutableMapOf<String, String>()
    file(fileName).readLines().forEach {
        val (key, value) = it.split("=")
        environmentVariables[key] = value
    }
    return environmentVariables
}