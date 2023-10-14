package org.gycoding.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.gycoding.controller.AppController

import org.gycoding.controller.Controller
import org.gycoding.model.data.Email
import org.gycoding.model.data.User

fun Application.configureRouting() {
    val appController: Controller   = AppController()

    var userTemp: String            = ""
    var passTemp: String            = ""

    routing {
        /*
        get("/user/{user}") {
            try {
                call.respondText(appController.getUser(call.parameters["user"]!!).toJSON())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/user/{email}") {
            try {
                call.respondText(appController.getUser(Email(call.parameters["email"]!!)).toJSON())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        */
        get("/login/{user}/{password}") {
            try {
                call.respondText(appController.checkLogin(call.parameters["user"]!!, call.parameters["password"]!!).toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/signup/{user}/{email}/{password}") {
            try {
                call.respondText(appController.signUp(User(call.parameters["user"]!!, Email(call.parameters["email"]!!)), call.parameters["password"]!!).toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
    }
}
