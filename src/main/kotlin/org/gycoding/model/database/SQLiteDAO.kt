package org.gycoding.model.database

import io.ktor.server.plugins.*
import org.gycoding.model.data.Email
import org.gycoding.model.data.User
import org.gycoding.model.utils.Cipher
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.sql.*
import java.util.*
import kotlin.collections.ArrayList

class SQLiteDAO() : DBDAO {
    private var conn: Connection? = null

    companion object {
        private val STATE_ERROR_USERNAME: Int   = -2;
        private val STATE_ERROR_EMAIL: Int      = -1;
        private val STATE_SUCCESS: Int          = 0;
    }

    init {
        Class.forName("org.sqlite.JDBC")
        conn = connect()
    }



    /* ================# PRIVATE FUNCTIONS #================ */

    /**
     * Connects to a database.
     * @return Connection instance.
     * @see Connection
     */
    fun connect(): Connection? {
        val URL                 = "jdbc:sqlite:identifier.db"
        val CONNECTION_SUCCESS  = "Connection set successfuly."

        return try {
            val conn: Connection = DriverManager.getConnection(URL)
            println(CONNECTION_SUCCESS)
            conn
        } catch (e: SQLException) {
            println(e.message)
            null
        }
    }

    /**
     * Process a complete SQL script into separated sentences.
     * @param script SQL script.
     * @return List of all the individual sentences from the script.
     * @throws IOException
     * @see MutableList
     */
    @Throws(IOException::class)
    private fun processSQL(script: String): MutableList<String> {
        var script = script
        val SQL_DELIM = ";"
        val scripts: MutableList<String> = ArrayList()
        script = script.replace("\\s".toRegex(), " ")
        val st = StringTokenizer(script, SQL_DELIM)

        while (st.hasMoreTokens()) {
            scripts.add(st.nextToken())
        }

        return scripts
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
        val ps: PreparedStatement = conn!!.prepareStatement(sql)

        val inputStreamPass = ByteArrayInputStream(pass)
        val inputStreamSalt = ByteArrayInputStream(salt)
        ps.setBinaryStream(1, inputStreamPass, pass.size)
        ps.setBinaryStream(2, inputStreamSalt, salt.size)
        ps.executeUpdate()
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
     * Reads byte data from the SQL database.
     * @param rs Set of results from a query selection to the SQL database.
     * @param column SQL database column which data will be read.
     * @return A byte array of al the data read.
     * @see ByteArray
     * @see ResultSet
     * @see ByteArrayOutputStream
     */
    private fun readBytes(rs: ResultSet, column: String): ByteArray {
        val inputStream: InputStream    = rs.getBinaryStream(column)
        val outputStream                = ByteArrayOutputStream()
        val buffer                      = ByteArray(4096)
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        return outputStream.toByteArray()
    }

    /**
     * Returns a user instance conditioned by the username given as parameter.
     * @param username Username over which the query sleection will be executed, returning the user instance of it.
     * @return User instance
     * @throws NotFoundException
     * @see User
     */
    @Throws(NotFoundException::class)
    private fun getUser(username: String): User {
        val QUERY_USER: String = "SELECT username, email, password, salt FROM Users WHERE username = \"${username}\""

        try {
            val rs: ResultSet = this.executeQuery(QUERY_USER)
            var user = User(rs.getString("username"), Email(rs.getString("email")))

            user.setPass(readBytes(rs, "password"))
            user.setSalt(readBytes(rs, "salt"))

            return user
        } catch(e: NullPointerException) {
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
    @Throws(NotFoundException::class)
    private fun getUser(email: Email): User {
        val QUERY_USER: String = "SELECT username, email, password, salt FROM Users WHERE email = \"${email}\""

        try {
            val rs: ResultSet = this.executeQuery(QUERY_USER)
            var user = User(rs.getString("username"), Email(rs.getString("email")))

            user.setPass(readBytes(rs, "password"))
            user.setSalt(readBytes(rs, "salt"))

            return user
        } catch(e: NullPointerException) {
            throw NotFoundException()
        }
    }



    /* ================# PUBLIC FUNCTIONS #================ */

    override public fun checkLogin(user: String, pass: String): Boolean {
        val user: User = this.getUser(user)
        return Cipher.verifyPassword(pass, user.getSalt(), user.getPass())
    }

    override public fun checkLogin(email: Email, pass: String): Boolean {
        val user: User = this.getUser(email)

        return Cipher.verifyPassword(pass, user.getSalt(), user.getPass())
    }

    @Throws(SQLException::class)
    override public fun signUp(user: User, pass: String): Int {
        var salt: ByteArray             = Cipher.generateSalt()

        val QUERY_INSERT_USER: String   =
            "INSERT INTO Users VALUES (" +
                    "\"${user.getUsername()}\"," +
                    "\"${user.getEmail()}\"," +
                    "?," +
                    "?" +
            ")"

        return try {
            executeByteInsert(QUERY_INSERT_USER, Cipher.hashPassword(pass, salt), salt)
            STATE_SUCCESS
        } catch(e: SQLException) {
            STATE_ERROR_EMAIL
        }
    }

    @Throws(SQLException::class)
    override fun updateUserPassword(user: User, pass: String) {
        val QUERY_UPDATE_PASSWORD: String = "UPDATE Users SET password = \"${pass}\" WHERE username = \"${user.getUsername()}\""

        try {
            executeUpdate(QUERY_UPDATE_PASSWORD)
        } catch(e: SQLException) {
            throw e
        }
    }

    @Throws(SQLException::class)
    override fun updateUserEmail(user: User, email: Email) {
        val QUERY_UPDATE_EMAIL: String = "UPDATE Users SET email = \"${email}\" WHERE username = \"${user.getUsername()}\""

        try {
            executeUpdate(QUERY_UPDATE_EMAIL)
        } catch(e: SQLException) {
            throw e
        }
    }
}