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
                call.respondText(appController.signUp(User(call.parameters["username"]!!, Email(call.parameters["email"]!!)), call.parameters["password"]!!).toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/update/user/{username}/{password}") {
            try {
                call.respondText(appController.updateUserUsername(call.parameters["username"]!!, call.parameters["password"]!!).toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/update/email/{username}/{email}/{password}") {
            try {
                call.respondText(appController.updateUserEmail(User(call.parameters["username"]!!, Email(call.parameters["email"]!!)), call.parameters["password"]!!).toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/update/password/{username}/{oldPassword}/{newPassword}") {
            try {
                call.respondText(appController.updateUserPassword(call.parameters["username"]!!, call.parameters["oldPassword"]!!, call.parameters["newPassword"]!!).toString())
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/team/{user}/{password}") {
            try {
                if(call.parameters["user"]!!.matches(Email.REGEX)) {
                    call.respondText(appController.getTeam(Email(call.parameters["user"]!!), call.parameters["password"]!!).toString())
                } else {
                    call.respondText(appController.getTeam(call.parameters["user"]!!, call.parameters["password"]!!).toString())
                }
            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
        get("/session/{user}/{password}") {
            try {
                if(call.parameters["user"]!!.matches(Email.REGEX)) {
                    call.respondText(appController.getSession(Email(call.parameters["user"]!!), call.parameters["password"]!!).toString())
                } else {
                    call.respondText(appController.getSession(call.parameters["user"]!!, call.parameters["password"]!!).toString())
                }

            } catch(e: NotFoundException) {
                call.respond(HttpStatusCode.NotFound, e.message.toString())
            }
        }
    }
}
