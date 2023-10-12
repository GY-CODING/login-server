package org.gycoding.controller

import org.gycoding.model.data.Email
import org.gycoding.model.data.User

interface Controller {
    fun checkLogin(username: String, pass: String): Boolean
    fun checkLogin(email: Email, pass: String): Boolean
    fun signUp(user: User, pass: String): Int
}