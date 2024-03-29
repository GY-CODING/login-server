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

    routing {
        get("/login/{user}/{password}") {
            try {
                if(call.parameters["user"]!!.matches(Email.REGEX)) {
                    call.respondText(appController.checkLogin(Email(call.parameters["user"]!!), call.parameters["password"]!!).toString())
                } else {
                    call.respondText(appController.checkLogin(call.parameters["user"]!!, call.parameters["password"]!!).toString())
                }
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/signup/{username}/{email}/{password}") {
            try {
                call.respondText(appController.signUp(User(call.parameters["username"]!!, Email(call.parameters["email"]!!)), call.parameters["password"]!!).value.toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/update/user/{username}/{newUsername}/{password}") {
            try {
                call.respondText(appController.updateUserUsername(call.parameters["username"]!!, call.parameters["newUsername"]!!, call.parameters["password"]!!).value.toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/update/email/{username}/{email}/{password}") {
            try {
                call.respondText(appController.updateUserEmail(User(call.parameters["username"]!!, Email(call.parameters["email"]!!)), call.parameters["password"]!!).value.toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/update/password/{username}/{oldPassword}/{newPassword}") {
            try {
                call.respondText(appController.updateUserPassword(call.parameters["username"]!!, call.parameters["oldPassword"]!!, call.parameters["newPassword"]!!).value.toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/update/password-forgotten/{username}/{email}/{newPassword}") {
            try {
                call.respondText(appController.updateUserPasswordForgotten(call.parameters["username"]!!, call.parameters["email"]!!, call.parameters["newPassword"]!!).value.toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
    }
}
