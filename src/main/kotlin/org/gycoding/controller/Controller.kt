package org.gycoding.controller

import org.gycoding.model.data.Email
import org.gycoding.model.data.User

interface Controller {
    fun getUser(username: String): User
    fun getUser(email: Email): User
}