package org.gycoding.model.database

import org.gycoding.model.data.Email
import org.gycoding.model.data.User
import java.io.IOException
import java.sql.*
import java.util.*
import kotlin.collections.ArrayList

class SQLiteDAO() : DBDAO {
    private var conn: Connection? = null

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

    override public fun getUser(username: String): User {
        val QUERY_USER: String = "SELECT username, email, password FROM Users WHERE username = \"${username}\""

        try {
            val rs: ResultSet = this.executeQuery(QUERY_USER)
            return User(rs.getString("username"), Email(rs.getString("email")), rs.getString("password"))
        } catch(e: Exception) {
            throw e
        }
    }

    override public fun getUser(email: Email): User {
        val QUERY_USER: String = "SELECT username, email, password FROM Users WHERE email = \"${email}\""

        try {
            val rs: ResultSet = this.executeQuery(QUERY_USER)
            return User(rs.getString("username"), Email(rs.getString("email")), rs.getString("password"))
        } catch(e: Exception) {
            throw e
        }
    }
}