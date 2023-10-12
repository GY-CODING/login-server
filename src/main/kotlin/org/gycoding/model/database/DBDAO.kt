package org.gycoding.model.database

import org.gycoding.model.data.Email
import org.gycoding.model.data.User

interface DBDAO {
    fun checkLogin(user: String, pass: String): Boolean
    fun checkLogin(email: Email, pass: String): Boolean
    fun signUp(user: User, pass: String): Int
    fun updateUserPassword(user: User, pass: String)
    fun updateUserEmail(user: User, email: Email)
}