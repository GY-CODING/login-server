package org.gycoding.controller

import io.ktor.server.plugins.*
import org.gycoding.model.data.Email
import org.gycoding.model.data.ServerState
import org.gycoding.model.data.User
import org.gycoding.model.database.DBDAO
import org.gycoding.model.database.DBFactory
import java.sql.SQLException

/**
 * Controlador de la aplicación.
 * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
 */
class AppController : Controller {
    var dbDAO: DBDAO? = null

    init {
        dbDAO = DBFactory.getDAO(DBFactory.MODE_MYSQL)
    }

    override fun checkLogin(username: String, pass: String): Boolean {
        try {
            return dbDAO!!.checkLogin(username, pass)
        } catch(e: NotFoundException) {
            throw e;
        }
    }

    override fun checkLogin(email: Email, pass: String): Boolean {
        try {
            return dbDAO!!.checkLogin(email, pass)
        } catch(e: NotFoundException) {
            throw e;
        }
    }

    override fun signUp(user: User, pass: String): ServerState {
        try {
            return dbDAO!!.signUp(user, pass)
        } catch(e: NotFoundException) {
            throw e;
        } catch(e: SQLException) {
            throw e;
        }
    }

    override fun updateUserUsername(username: String, newUsername: String, pass: String): ServerState {
        try {
            return dbDAO!!.updateUserUsername(username, newUsername, pass)
        } catch(e: NotFoundException) {
            throw e;
        } catch(e: SQLException) {
            throw e;
        }
    }

    override fun updateUserPassword(username: String, oldPass: String, newPass: String): ServerState {
        try {
            return dbDAO!!.updateUserPassword(username, oldPass, newPass)
        } catch(e: NotFoundException) {
            throw e;
        } catch(e: SQLException) {
            throw e;
        }
    }

    override fun updateUserPasswordForgotten(username: String, email: String, newPass: String): ServerState {
        try {
            return dbDAO!!.updateUserPasswordForgotten(username, email, newPass)
        } catch(e: NotFoundException) {
            throw e;
        } catch(e: SQLException) {
            throw e;
        }
    }

    override fun updateUserEmail(user: User, pass: String): ServerState {
        try {
            return dbDAO!!.updateUserEmail(user, pass)
        } catch(e: NotFoundException) {
            throw e;
        } catch(e: SQLException) {
            throw e;
        }
    }
}