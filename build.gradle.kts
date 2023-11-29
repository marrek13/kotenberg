plugins {
    kotlin("jvm") version "1.9.21"
}

group = "dev.marrek13"
version = "1.0.0"

dependencies {
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.httpcomponents:httpmime:4.5.13")
    implementation("commons-io:commons-io:2.11.0")
}

tasks{
    test {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
}
