plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.spotless)
}

group = "dev.marrek13"
version = "1.0.0"

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    implementation(libs.commons.io)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.testcontainers.jupiter)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("${layout.buildDirectory.asFile.get()}/**/*.kt")
        ktlint()
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
