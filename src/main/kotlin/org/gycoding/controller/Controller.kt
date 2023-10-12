package org.gycoding.controller

import org.gycoding.model.data.Email
import org.gycoding.model.data.User
import java.sql.SQLException

interface Controller {
    /**
     * Evaluates if an user exists on our database and if the login parameters are correct, if so, gives access to the account.
     * @param username Username of the user.
     * @param pass Password to try to login to the account.
     * @return Whether the user can or can not access to the account.
     */
    fun checkLogin(username: String, pass: String): Boolean

    /**
     * Evaluates if an user exists on our database and if the login parameters are correct, if so, gives access to the account.
     * @param email Email of the user (any service is allowed).
     * @param pass Password to try to login to the account.
     * @return Whether the user can or can not access to the account.
     */
    fun checkLogin(email: Email, pass: String): Boolean

    /**
     * Registers an user to the database.
     * @param user User object (containing all of the necessary data for the registration process).
     * @param pass Password of the user.
     * @return Integer representing the status of the registration.
     * @throws SQLException
     * @see ByteArray
     */
    fun signUp(user: User, pass: String): Int
}