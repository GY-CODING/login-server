package org.gycoding.model.database

import org.gycoding.model.data.Email
import org.gycoding.model.data.User

interface DBDAO {
    fun getUser(username: String): User
    fun getUser(email: Email): User
    fun insertUser(user: User)
    fun updateUserPassword(user: User, pass: String)
    fun updateUserEmail(user: User, email: Email)
}