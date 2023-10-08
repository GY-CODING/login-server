package org.gycoding.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.gycoding.controller.AppController

import org.gycoding.controller.Controller

fun Application.configureRouting() {
    routing {
        get("/toxyc") {
            val appController: Controller = AppController()
            call.respondText(appController.getUser("toxyc").toJSON())
        }
        get("/ws1") {
            val appController: Controller = AppController()
            call.respondText(appController.getUser("ws1").toJSON())
        }
        get("/jose55") {
            val appController: Controller = AppController()
            call.respondText(appController.getUser("jose55").toJSON())
        }
        get("/ainhoa") {
            val appController: Controller = AppController()
            call.respondText(appController.getUser("ainhoapqueña").toJSON())
        }
    }
}
