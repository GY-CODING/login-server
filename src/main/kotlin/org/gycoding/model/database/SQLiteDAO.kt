package org.gycoding.model.database

import io.ktor.server.plugins.*
import io.ktor.utils.io.charsets.Charset
import org.gycoding.model.data.Email
import org.gycoding.model.data.User
import org.gycoding.model.utils.Cipher
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.sql.*
import java.util.*
import kotlin.collections.ArrayList

class SQLiteDAO() : DBDAO {
    private var conn: Connection? = null

    companion object {
        private val STATE_ERROR: Int = -1;
        private val STATE_SUCCESS: Int = 0;
    }

    init {
        Class.forName("org.sqlite.JDBC")
        conn = connect()
    }

    /**
     * Establece la conexión a la base de datos.
     * @return Objeto de conexión.
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
     */
    @Throws(IOException::class)
    private fun processSQL(script: String): List<String> {
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
     * Ejecuta una sentencia DML de inserción de datos de SQL.
     * @param sql Sentencia individual de SQL.
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

    private fun executeByteInsert(sql: String, pass: ByteArray, salt: ByteArray) {
        val ps: PreparedStatement = conn!!.prepareStatement(sql)

        val inputStreamPass = ByteArrayInputStream(pass)
        val inputStreamSalt = ByteArrayInputStream(salt)
        ps.setBinaryStream(1, inputStreamPass, pass.size)
        ps.setBinaryStream(2, inputStreamSalt, salt.size)
        ps.executeUpdate()
    }

    /**
     * Ejecuta una sentencia DML de actualización de datos de SQL.
     * @param sql Sentencia individual de SQL.
     * @throws SQLException
     */
    @Throws(SQLException::class)
    private fun executeUpdate(sql: String) {
        val ps: PreparedStatement = conn!!.prepareStatement(sql)
        ps.executeUpdate()
        ps.close()
    }

    /**
     * Ejecuta una selección SQL.
     * @param sql Sentencia individual de SQL.
     * @return
     * @throws SQLException
     * @see ResultSet
     */
    @Throws(SQLException::class)
    private fun executeQuery(sql: String): ResultSet {
        val ps: PreparedStatement = conn!!.prepareStatement(sql)
        return ps.executeQuery()
    }





    @Throws(NotFoundException::class)
    private fun getUser(username: String): User {
        val QUERY_USER: String = "SELECT username, email, password, salt FROM Users WHERE username = \"${username}\""

        try {
            val rs: ResultSet = this.executeQuery(QUERY_USER)
            var user = User(rs.getString("username"), Email(rs.getString("email")))

            val inputStreamPass: InputStream = rs.getBinaryStream("password")
            val inputStreamSalt: InputStream = rs.getBinaryStream("salt")

            val outputStreamPass = ByteArrayOutputStream()
            val outputStreamSalt = ByteArrayOutputStream()

            val buffer = ByteArray(4096)

            var bytesReadPass: Int
            var bytesReadSalt: Int

            while (inputStreamPass.read(buffer).also { bytesReadPass = it } != -1) {
                outputStreamPass.write(buffer, 0, bytesReadPass)
            }

            while (inputStreamSalt.read(buffer).also { bytesReadSalt = it } != -1) {
                outputStreamSalt.write(buffer, 0, bytesReadSalt)
            }

            user.setPass(outputStreamPass.toByteArray())
            user.setSalt(outputStreamSalt.toByteArray())

            return user
        } catch(e: NullPointerException) {
            throw NotFoundException()
        }
    }

    @Throws(NotFoundException::class)
    private fun getUser(email: Email): User {
        val QUERY_USER: String = "SELECT username, email, password, salt FROM Users WHERE email = \"${email}\""

        try {
            val rs: ResultSet = this.executeQuery(QUERY_USER)
            var user = User(rs.getString("username"), Email(rs.getString("email")))

            user.setPass(rs.getString("password").toByteArray(Charsets.UTF_8))
            user.setSalt(rs.getString("salt").toByteArray(Charsets.UTF_8))

            return user
        } catch(e: NullPointerException) {
            throw NotFoundException()
        }
    }





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
        var state: Int                  = -1;
        var salt: ByteArray             = Cipher.generateSalt()

        val QUERY_INSERT_USER: String   =
            "INSERT INTO Users VALUES (" +
                    "\"${user.getUsername()}\"," +
                    "\"${user.getEmail()}\"," +
                    "?," +
                    "?" +
            ")"

        state = try {
            executeByteInsert(QUERY_INSERT_USER, Cipher.hashPassword(pass, salt), salt)
            STATE_SUCCESS
        } catch(e: SQLException) {
            STATE_ERROR
        }

        return state
    }

    override fun updateUserPassword(user: User, pass: String) {
        val QUERY_INSERT_USER: String = ""

        executeInsert(QUERY_INSERT_USER)
    }

    override fun updateUserEmail(user: User, email: Email) {
        TODO("Not yet implemented")
    }
}