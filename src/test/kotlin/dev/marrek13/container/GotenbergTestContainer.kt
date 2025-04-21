package dev.marrek13.container

import org.testcontainers.containers.GenericContainer

class GotenbergTestContainer : GenericContainer<GotenbergTestContainer>("gotenberg/gotenberg:8") {
    val endpoint: String
        get() = "http://localhost:${getMappedPort(3000)}/"

    init {
        withExposedPorts(3000)
    }
}
