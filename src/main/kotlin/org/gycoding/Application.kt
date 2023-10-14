package org.gycoding

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.gycoding.plugins.configureHTTP
import org.gycoding.plugins.configureRouting

fun main() {
    embeddedServer(Netty, port = 8080, host = "26.62.131.11", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureRouting()
}
