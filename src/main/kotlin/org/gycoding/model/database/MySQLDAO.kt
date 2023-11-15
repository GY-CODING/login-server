package org.gycoding.model.database

import io.ktor.server.plugins.*
import org.gycoding.model.data.Email
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
    override public fun signUp(user: User, pass: String): Int {
        val salt: ByteArray = Cipher.generateSalt()

        return try {
            getUser(user.getUsername())
            STATE_ERROR_USERNAME
        } catch(e: NotFoundException) {
            return try {
                getUser(user.getEmail())
                STATE_ERROR_EMAIL
            } catch(e: NotFoundException) {
                val QUERY_INSERT_USER: String = "INSERT INTO User VALUES (\"${user.getUsername()}\", \"${user.getEmail()}\", ?, ?, \"${user.getRole()}\")"

                return try {
                    executeByteInsert(QUERY_INSERT_USER, Cipher.hashPassword(pass, salt), salt)
                    STATE_SUCCESS
                } catch(e: SQLException) {
                    STATE_ERROR_DATABASE
                }
            }
        }
    }

    @Throws(SQLException::class)
    override fun updateUserPassword(user: User, oldPass: String, newPass: String): Int {
        val QUERY_UPDATE_PASSWORD: String = "UPDATE User SET password = \"${Cipher.hashPassword(newPass, user.getSalt())}\" WHERE username = \"${user.getUsername()}\""

        return try {
            if(Cipher.verifyPassword(oldPass, user.getSalt(), user.getPass())) {
                executeUpdate(QUERY_UPDATE_PASSWORD)
                STATE_SUCCESS;
            } else {
                STATE_ERROR_PASSWORD
            }
        } catch(e: SQLException) {
            STATE_ERROR_DATABASE
        }
    }

    @Throws(SQLException::class)
    override fun updateUserEmail(user: User, email: Email): Int {
        val QUERY_UPDATE_EMAIL: String = "UPDATE User SET email = \"${email}\" WHERE username = \"${user.getUsername()}\""

        return try {
            executeUpdate(QUERY_UPDATE_EMAIL)
            STATE_SUCCESS
        } catch(e: SQLException) {
            STATE_ERROR_DATABASE
        }
    }
}