package org.gycoding.controller

import io.ktor.server.plugins.*
import org.gycoding.model.data.Email
import org.gycoding.model.data.User
import org.gycoding.model.database.DBDAO
import org.gycoding.model.database.DBFactory
import java.sql.SQLException

class AppController : Controller {
    var dbDAO: DBDAO? = null

    init {
        dbDAO = DBFactory.getDAO(DBFactory.MODE_SQLITE)
    }

    override fun checkLogin(user: String, pass: String): Boolean {
        try {
            return dbDAO!!.checkLogin(user, pass)
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

    override fun signUp(user: User, pass: String): Int {
        try {
            return dbDAO!!.signUp(user, pass)
        } catch(e: NotFoundException) {
            throw e;
        } catch(e: SQLException) {
            throw e;
        }
    }
}