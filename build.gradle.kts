plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktfmt)
    id("maven-publish")
}

group = "dev.marrek13"

dependencies {
    api(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    implementation(libs.commons.io)

    testImplementation(libs.junit.jupiter.core)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.platform.launcher)
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

ktfmt {
    kotlinLangStyle()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/marrek13/kotenberg")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
