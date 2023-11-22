package org.gycoding.model.database

import io.ktor.server.plugins.*
import org.gycoding.model.data.Email
import org.gycoding.model.data.ServerState
import org.gycoding.model.data.User
import org.gycoding.model.utils.ByteHexConverter
import org.gycoding.model.utils.Cipher
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.sql.*
import java.util.*
import kotlin.collections.ArrayList

class MySQLDAO() : DBDAO {
    private var conn: Connection? = null

    companion object {
        val STATE_ERROR_USERNAME: Int   = -3;
        val STATE_ERROR_EMAIL: Int      = -2;
        val STATE_ERROR_PASSWORD: Int   = -1;
        val STATE_ERROR_DATABASE: Int   = 0;
        val STATE_SUCCESS: Int          = 1;
    }

    init {
        Class.forName("com.mysql.cj.jdbc.Driver")
        conn = connect()
    }



    /* ================# PRIVATE FUNCTIONS #================ */

    /**
     * Connects to a database.
     * @return Connection instance.
     * @see Connection
     */
    fun connect(): Connection? {
        val URL: String             = "ivm-accounts.ch53lvtsczj2.eu-west-3.rds.amazonaws.com"
        val PORT: Int               = 3306
        val DATABASE_NAME: String   = "ivmaccounts"
        val USER: String            = "admin"
        val PASSWORD: String        = "ivmmanager"

        val PATH                = "jdbc:mysql://${URL}:${PORT}/${DATABASE_NAME}"
        val CONNECTION_SUCCESS  = "Connection set successfuly."

        return try {
            val conn: Connection = DriverManager.getConnection(PATH, USER, PASSWORD)
            println(CONNECTION_SUCCESS)
            conn
        } catch (e: SQLException) {
            println(e.message)
            null
        }
    }

    /**
     * Executes a DML sentence of insert to the SQL database.
     * @param sql SQL sentence (scripts are not allowed).
     * @throws SQLException
     */
    @Throws(SQLException::class)
    private fun executeInsert(sql: String) {
        val ps: PreparedStatement = conn!!.prepareStatement(sql)
        ps.addBatch()
        conn!!.setAutoCommit(false)
        ps.executeBatch()
        conn!!.setAutoCommit(true)
        ps.close()
    }

    /**
     * Executes a DML sentence of insert to the SQL database,
     * where the data is specifically thought to be a hashed password and its salt.
     * @param sql SQL sentence (scripts are not allowed).
     * @param pass Hashed password.
     * @param salt User associated salt.
     * @throws SQLException
     * @see ByteArray
     */
    @Throws(SQLException::class)
    private fun executeByteInsert(sql: String, pass: ByteArray, salt: ByteArray) {
        try {
            val preparedStatement: PreparedStatement = this.conn!!.prepareStatement(sql)

            preparedStatement.setString(1, ByteHexConverter.bytesToHex(pass))
            preparedStatement.setString(2, ByteHexConverter.bytesToHex(salt))

            preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            throw e
        }
    }

    /**
     * Executes a DML sentence of update to the SQL database.
     * @param sql SQL sentence (scripts are not allowed).
     * @throws SQLException
     */
    @Throws(SQLException::class)
    private fun executeUpdate(sql: String) {
        val ps: PreparedStatement = conn!!.prepareStatement(sql)
        ps.executeUpdate()
        ps.close()
    }

    /**
     * Executes a query selection from the SQL database.
     * @param sql SQL sentence (scripts are not allowed).
     * @return Set of results given by the query selection.
     * @throws SQLException
     * @see ResultSet
     */
    @Throws(SQLException::class)
    private fun executeQuery(sql: String): ResultSet {
        val ps: PreparedStatement = conn!!.prepareStatement(sql)
        return ps.executeQuery()
    }

    /**
     * Returns a user instance conditioned by the username given as parameter.
     * @param username Username over which the query sleection will be executed, returning the user instance of it.
     * @return User instance
     * @throws NotFoundException
     * @see User
     */
    @Throws(Exception::class)
    private fun getUser(username: String): User? {
        val QUERY_USER: String      = "SELECT username, email, password, salt, role FROM User WHERE username = \"${username}\""

        try {
            val rs: ResultSet       = this.executeQuery(QUERY_USER)
            var user: User?         = null

            while(rs.next()) {
                user = User(rs.getString("username"), Email(rs.getString("email")))

                user.setPass(ByteHexConverter.hexToBytes(rs.getString("password")))
                user.setSalt(ByteHexConverter.hexToBytes(rs.getString("salt")))
                user.setRole(rs.getString("role"))
            }

            return user
        } catch(e: Exception) {
            e.printStackTrace()
            throw NotFoundException()
        }
    }

    /**
     * Returns a user instance condition by the email given as parameter.
     * @param email Email over which the query sleection will be executed, returning the user instance of it (any service is allowed).
     * @return User instance
     * @throws NotFoundException
     * @see User
     */
    @Throws(Exception::class)
    private fun getUser(email: Email): User? {
        val QUERY_USER: String      = "SELECT username, email, password, salt, role FROM User WHERE email = \"${email}\""

        try {
            val rs: ResultSet       = this.executeQuery(QUERY_USER)
            var user: User?         = null

            while(rs.next()) {
                user = User(rs.getString("username"), Email(rs.getString("email")))

                user.setPass(ByteHexConverter.hexToBytes(rs.getString("password")))
                user.setSalt(ByteHexConverter.hexToBytes(rs.getString("salt")))
                user.setRole(rs.getString("role"))
            }

            return user
        } catch(e: Exception) {
            e.printStackTrace()
            throw NotFoundException()
        }
    }

    @Throws(Exception::class)
    private fun getUserTeam(username: String): MutableList<Int> {
        val QUERY_USER_TEAM: String      = "SELECT teamElement1, teamElement2, teamElement3, teamElement4, teamElement5, teamElement6, teamElement7, teamElement8 FROM User WHERE username = \"${username}\""

        try {
            val rs: ResultSet            = this.executeQuery(QUERY_USER_TEAM)
            var team: MutableList<Int>   = ArrayList<Int>()

            while(rs.next()) {
                team.add(rs.getInt("teamElement1"))
                team.add(rs.getInt("teamElement2"))
                team.add(rs.getInt("teamElement3"))
                team.add(rs.getInt("teamElement4"))
                team.add(rs.getInt("teamElement5"))
                team.add(rs.getInt("teamElement6"))
                team.add(rs.getInt("teamElement7"))
                team.add(rs.getInt("teamElement8"))
            }

            return team
        } catch(e: Exception) {
            e.printStackTrace()
            throw NotFoundException()
        }
    }

    @Throws(Exception::class)
    private fun getUserTeam(email: Email): MutableList<Int> {
        val QUERY_USER_TEAM: String      = "SELECT teamElement1, teamElement2, teamElement3, teamElement4, teamElement5, teamElement6, teamElement7, teamElement8 FROM User WHERE email = \"${email}\""

        try {
            val rs: ResultSet            = this.executeQuery(QUERY_USER_TEAM)
            var team: MutableList<Int>   = ArrayList<Int>()

            while(rs.next()) {
                team.add(rs.getInt("teamElement1"))
                team.add(rs.getInt("teamElement2"))
                team.add(rs.getInt("teamElement3"))
                team.add(rs.getInt("teamElement4"))
                team.add(rs.getInt("teamElement5"))
                team.add(rs.getInt("teamElement6"))
                team.add(rs.getInt("teamElement7"))
                team.add(rs.getInt("teamElement8"))
            }

            return team
        } catch(e: Exception) {
            e.printStackTrace()
            throw NotFoundException()
        }
    }



    /* ================# PUBLIC FUNCTIONS #================ */

    override public fun checkLogin(user: String, pass: String): Boolean {
        val user: User = this.getUser(user)!!
        return Cipher.verifyPassword(pass, user.getSalt(), user.getPass())
    }

    override public fun checkLogin(email: Email, pass: String): Boolean {
        val user: User = this.getUser(email)!!
        return Cipher.verifyPassword(pass, user.getSalt(), user.getPass())
    }

    @Throws(SQLException::class)
    override public fun signUp(user: User, pass: String): ServerState {
        val salt: ByteArray = Cipher.generateSalt()

        return try {
            getUser(user.getUsername())
            ServerState.STATE_ERROR_USERNAME
        } catch(e: NotFoundException) {
            return try {
                getUser(user.getEmail())
                ServerState.STATE_ERROR_EMAIL
            } catch(e: NotFoundException) {
                val QUERY_INSERT_USER: String = "INSERT INTO User VALUES (\"${user.getUsername()}\", \"${user.getEmail()}\", ?, ?, \"${user.getRole()}\")"

                return try {
                    executeByteInsert(QUERY_INSERT_USER, Cipher.hashPassword(pass, salt), salt)
                    ServerState.STATE_SUCCESS
                } catch(e: SQLException) {
                    ServerState.STATE_ERROR_DATABASE
                }
            }
        }
    }

    @Throws(SQLException::class)
    override fun updateUserUsername(username: String, pass: String): ServerState {
        val QUERY_UPDATE_USERNAME: String = "UPDATE User SET username = \"${username}\" WHERE username = \"${username}\""

        return if(this.checkLogin(username, pass)) {
            return try {
                executeUpdate(QUERY_UPDATE_USERNAME)
                ServerState.STATE_SUCCESS
            } catch (e: SQLException) {
                ServerState.STATE_ERROR_DATABASE
            }
        } else {
            ServerState.STATE_ERROR_PASSWORD
        }
    }

    @Throws(SQLException::class)
    override fun updateUserPassword(username: String, oldPass: String, newPass: String): ServerState {
        val tempUser: User = this.getUser(username)!!
        val QUERY_UPDATE_PASSWORD: String = "UPDATE User SET password = \"${Cipher.hashPassword(newPass, tempUser.getSalt())}\" WHERE username = \"${username}\""

        return if(this.checkLogin(username, oldPass)) {
            return try {
                executeUpdate(QUERY_UPDATE_PASSWORD)
                ServerState.STATE_SUCCESS
            } catch (e: SQLException) {
                ServerState.STATE_ERROR_DATABASE
            }
        } else {
            ServerState.STATE_ERROR_PASSWORD
        }
    }

    @Throws(SQLException::class)
    override fun updateUserEmail(user: User, pass: String): ServerState {
        val QUERY_UPDATE_EMAIL: String = "UPDATE User SET email = \"${user.getEmail()}\" WHERE username = \"${user.getUsername()}\""

        return if(this.checkLogin(user.getUsername(), pass)) {
            return try {
                executeUpdate(QUERY_UPDATE_EMAIL)
                ServerState.STATE_SUCCESS
            } catch (e: SQLException) {
                ServerState.STATE_ERROR_DATABASE
            }
        } else {
            ServerState.STATE_ERROR_PASSWORD
        }
    }

    override fun getSession(username: String, pass: String): User? {
        return if(this.checkLogin(username, pass)) {
            this.getUser(username)
        } else {
            null
        }
    }

    override fun getSession(email: Email, pass: String): User? {
        return if(this.checkLogin(email, pass)) {
            this.getUser(email)
        } else {
            null
        }
    }

    override fun getTeam(username: String, pass: String): String? {
        return if(this.checkLogin(username, pass)) {
            this.getUserTeam(username).joinToString(";")
        } else {
            null
        }
    }

    override fun getTeam(email: Email, pass: String): String? {
        return if(this.checkLogin(email, pass)) {
            this.getUserTeam(email).joinToString(";")
        } else {
            null
        }
    }

    override fun setTeam(username: String, pass: String, team: List<Int>): ServerState {
        val QUERY_UPDATE_TEAMS: String = "UPDATE User SET teamElement1 = ${team[0]}, teamElement2 = ${team[1]}, teamElement3 = ${team[2]}, teamElement4 = ${team[3]}, teamElement5 = ${team[4]}, teamElement6 = ${team[5]}, teamElement7 = ${team[6]}, teamElement8 = ${team[7]} WHERE username = \"${username}\""

        return if(this.checkLogin(username, pass)) {
            return try {
                executeUpdate(QUERY_UPDATE_TEAMS)
                ServerState.STATE_SUCCESS
            } catch (e: SQLException) {
                ServerState.STATE_ERROR_DATABASE
            }
        } else {
            ServerState.STATE_ERROR_PASSWORD
        }
    }

    override fun setTeam(email: Email, pass: String, team: List<Int>): ServerState {
        val QUERY_UPDATE_TEAMS: String = "UPDATE User SET teamElement1 = ${team[0]}, teamElement2 = ${team[1]}, teamElement3 = ${team[2]}, teamElement4 = ${team[3]}, teamElement5 = ${team[4]}, teamElement6 = ${team[5]}, teamElement7 = ${team[6]}, teamElement8 = ${team[7]} WHERE email = \"${email.toString()}\""

        return if(this.checkLogin(email, pass)) {
            return try {
                executeUpdate(QUERY_UPDATE_TEAMS)
                ServerState.STATE_SUCCESS
            } catch (e: SQLException) {
                ServerState.STATE_ERROR_DATABASE
            }
        } else {
            ServerState.STATE_ERROR_PASSWORD
        }
    }
}