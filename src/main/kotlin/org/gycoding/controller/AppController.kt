package org.gycoding.controller

import org.gycoding.model.data.Email
import org.gycoding.model.data.User
import org.gycoding.model.database.DBDAO
import org.gycoding.model.database.DBFactory

class AppController : Controller {
    var dbDAO: DBDAO? = null

    init {
        dbDAO = DBFactory.getDAO(DBFactory.MODE_SQLITE)
    }

    override fun getUser(username: String): User {
        return dbDAO!!.getUser(username)
    }

    override fun getUser(email: Email): User {
        return dbDAO!!.getUser(email)
    }
}