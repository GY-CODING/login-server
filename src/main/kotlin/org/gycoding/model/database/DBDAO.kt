package org.gycoding.model.database

import org.gycoding.model.data.Email
import org.gycoding.model.data.ServerState
import org.gycoding.model.data.User
import java.sql.SQLException

interface DBDAO {
    /**
     * Evaluates if an user exists on our database and if the login parameters are correct, if so, gives access to the account.
     * @param user User name.
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
    fun signUp(user: User, pass: String): ServerState

    /**
     * Updates the username of an user from the database.
     * @param username
     * @param pass User's password.
     * @return Integer representing the status of the data update.
     * @throws SQLException
     */
    fun updateUserUsername(username: String, pass: String): ServerState

    /**
     * Updates the password of an user from the database.
     * @param user User object (containing all of the necessary data for the registration process).
     * @param oldPass Old password.
     * @param newPass New password.
     * @return Integer representing the status of the data update.
     * @throws SQLException
     */
    fun updateUserPassword(username: String, oldPass: String, newPass: String): ServerState

    /**
     * Updates the password of an user from the database.
     * @param user User object (containing all of the necessary data for the registration process).
     * @param pass Current user password.
     * @return Integer representing the status of the data update.
     * @throws SQLException
     */
    fun updateUserEmail(user: User, pass: String): ServerState

    fun getTeam(username: String, pass: String): String?

    fun getTeam(email: Email, pass: String): String?

    fun setTeam(username: String, pass: String, team: List<Int>): ServerState

    fun setTeam(email: Email, pass: String, team: List<Int>): ServerState

    fun getSession(username: String, pass: String): User?

    fun getSession(email: Email, pass: String): User?
}