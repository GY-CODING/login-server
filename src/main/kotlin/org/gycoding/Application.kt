package org.gycoding

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.gycoding.plugins.configureHTTP
import org.gycoding.plugins.configureRouting

fun main() {
    embeddedServer(Netty, port = System.getenv("KTOR_PORT").toInt(), host = System.getenv("KTOR_IP"), module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureRouting()
}
