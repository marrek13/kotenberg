import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.javadoc.Javadoc

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktfmt)
    id("maven-publish")
    signing
}

group = "dev.marrek13"

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.named<Javadoc>("javadoc") { isFailOnError = false }

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

tasks { test { useJUnitPlatform() } }

repositories { mavenCentral() }

ktfmt { kotlinLangStyle() }

fun MavenPublication.configureKotenbergPom() {
    pom {
        name = "Kotenberg"
        description.set("Type-safe Kotlin client for the Gotenberg document conversion HTTP API.")
        url.set("https://github.com/marrek13/kotenberg")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("marrek13")
                name.set("Mariusz Sołtysiak")
                url.set("https://github.com/marrek13")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/marrek13/kotenberg.git")
            developerConnection.set("scm:git:ssh://git@github.com/marrek13/kotenberg.git")
            url.set("https://github.com/marrek13/kotenberg")
        }
    }
}

val mavenCentralUsername =
    System.getenv("MAVEN_CENTRAL_USERNAME") ?: findProperty("mavenCentralUsername")?.toString()
val mavenCentralPassword =
    System.getenv("MAVEN_CENTRAL_PASSWORD") ?: findProperty("mavenCentralPassword")?.toString()
val signingKeyForCentral = System.getenv("SIGNING_KEY") ?: findProperty("signingKey")?.toString()

if (
    !mavenCentralUsername.isNullOrBlank() &&
        !mavenCentralPassword.isNullOrBlank() &&
        signingKeyForCentral.isNullOrBlank()
) {
    logger.error(
        "MAVEN_CENTRAL_USERNAME and MAVEN_CENTRAL_PASSWORD are set but SIGNING_KEY is missing; " +
            "Maven Central requires signed artifacts."
    )
    throw GradleException(
        "Configure SIGNING_KEY (and SIGNING_PASSWORD if the key is encrypted) for Maven Central."
    )
}

publishing {
    publications {
        create<MavenPublication>("mavenGithub") {
            groupId = group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            configureKotenbergPom()
        }
        create<MavenPublication>("mavenCentral") {
            groupId = "io.github.marrek13"
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            configureKotenbergPom()
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
        val centralUser = mavenCentralUsername
        val centralPassword = mavenCentralPassword
        if (!centralUser.isNullOrBlank() && !centralPassword.isNullOrBlank()) {
            maven {
                name = "MavenCentral"
                url =
                    uri(
                        "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/"
                    )
                credentials {
                    username = centralUser
                    password = centralPassword
                }
            }
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    enabled =
        when (repository.name) {
            "GitHubPackages" -> publication.name == "mavenGithub"
            "MavenCentral" -> publication.name == "mavenCentral"
            else -> false
        }
}

signing {
    val signingKey = signingKeyForCentral
    val signingPassword =
        System.getenv("SIGNING_PASSWORD") ?: findProperty("signingPassword")?.toString()
    if (!signingKey.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["mavenCentral"])
    }
}
