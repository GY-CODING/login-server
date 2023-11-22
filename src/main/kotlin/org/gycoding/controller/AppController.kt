package org.gycoding.controller

import io.ktor.server.plugins.*
import org.gycoding.model.data.Email
import org.gycoding.model.data.ServerState
import org.gycoding.model.data.User
import org.gycoding.model.database.DBDAO
import org.gycoding.model.database.DBFactory
import java.sql.SQLException

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

    override fun updateUserUsername(username: String, pass: String): ServerState {
        try {
            return dbDAO!!.updateUserUsername(username, pass)
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

    override fun updateUserEmail(user: User, pass: String): ServerState {
        try {
            return dbDAO!!.updateUserEmail(user, pass)
        } catch(e: NotFoundException) {
            throw e;
        } catch(e: SQLException) {
            throw e;
        }
    }

    override fun getTeam(username: String, pass: String): String? {
        try {
            return dbDAO!!.getTeam(username, pass)
        } catch(e: NotFoundException) {
            throw e;
        }
    }

    override fun getTeam(email: Email, pass: String): String? {
        try {
            return dbDAO!!.getTeam(email, pass)
        } catch(e: NotFoundException) {
            throw e;
        }
    }

    override fun setTeam(username: String, pass: String, team: List<Int>): ServerState {
        try {
            return dbDAO!!.setTeam(username, pass, team)
        } catch(e: NotFoundException) {
            throw e;
        }
    }

    override fun setTeam(email: Email, pass: String, team: List<Int>): ServerState {
        try {
            return dbDAO!!.setTeam(email, pass, team)
        } catch(e: NotFoundException) {
            throw e;
        }
    }

    override fun getSession(username: String, pass: String): User? {
        try {
            return dbDAO!!.getSession(username, pass)
        } catch(e: NotFoundException) {
            throw e;
        }
    }

    override fun getSession(email: Email, pass: String): User? {
        try {
            return dbDAO!!.getSession(email, pass)
        } catch(e: NotFoundException) {
            throw e;
        }
    }
}