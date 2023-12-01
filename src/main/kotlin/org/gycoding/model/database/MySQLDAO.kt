package org.gycoding.model.database

import io.ktor.network.sockets.*
import io.ktor.server.plugins.*
import org.gycoding.model.data.Email
import org.gycoding.model.data.ServerState
import org.gycoding.model.data.User
import org.gycoding.model.utils.ByteHexConverter
import org.gycoding.model.utils.Cipher
import java.sql.*
import java.sql.Connection
import java.util.*

/**
 * Objeto de acceso a datos de la base de datos en AWS RDS con MySQL.
 * @author Iván Vicente Morales
 * @see <a href="https://aws.amazon.com/es/rds/">Amazon Web Services RDS</a>
 */
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
    }



    /* ================# PRIVATE FUNCTIONS #================ */

    /**
     * Conecta a una base de datos.
     * @return Instancia de la conexión.
     * @see Connection
     * @author Iván Vicente Morales
     */
    fun connect(): Connection? {
        val URL: String             = System.getenv("MYSQL_URL")
        val PORT: Int               = System.getenv("MYSQL_PORT").toInt()
        val DATABASE_NAME: String   = System.getenv("MYSQL_DB")
        val USER: String            = System.getenv("MYSQL_USER")
        val PASSWORD: String        = System.getenv("MYSQL_PASS")

        val PATH                = "jdbc:mysql://${URL}:${PORT}/${DATABASE_NAME}"
        val CONNECTION_SUCCESS  = "Connection set successfuly."

        return try {
            val conn: Connection = DriverManager.getConnection(PATH, USER, PASSWORD)
            println(CONNECTION_SUCCESS)
            conn
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Ejecuta una inserción en la base de datos.
     * @param sql Sentencia SQL (no admite scripts).
     * @throws SQLException
     * @author Iván Vicente Morales
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
     * Ejecuta una inserción binaria en la base de datos.
     * @param sql Sentencia SQL (no admite scripts).
     * @param pass Contraseña cifrada.
     * @param salt Sal cifrada asociada al usuario.
     * @throws SQLException
     * @see ByteArray
     * @author Iván Vicente Morales
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
     * Ejecuta una modificación a un registro de la base de datos.
     * @param sql Sentencia SQL (no admite scripts).
     * @throws SQLException
     * @author Iván Vicente Morales
     */
    @Throws(SQLException::class)
    private fun executeUpdate(sql: String) {
        val ps: PreparedStatement = conn!!.prepareStatement(sql)
        ps.executeUpdate()
        ps.close()
    }

    /**
     * Ejecuta una selección a la base de datos.
     * @param sql Sentencia SQL (no admite scripts).
     * @return Conjunto de resultados que devuelve la consulta.
     * @throws SQLException
     * @see ResultSet
     * @author Iván Vicente Morales
     */
    @Throws(SQLException::class)
    private fun executeQuery(sql: String): ResultSet {
        val ps: PreparedStatement = conn!!.prepareStatement(sql)
        return ps.executeQuery()
    }

    /**
     * Devuelve un usuario.
     * @param username Nombre del usuario
     * @return Usuario
     * @throws NotFoundException
     * @see User
     * @author Iván Vicente Morales
     */
    @Throws(NotFoundException::class)
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
     * Devuelve un usuario.
     * @param email Email del usuario.
     * @return Usuario
     * @throws NotFoundException
     * @see User
     * @author Iván Vicente Morales
     */
    @Throws(NotFoundException::class)
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

    /**
     * Devuelve el equipo de un usuario como una lista mutable de identificadores de cada personaje.
     * @param username Nombre del usuario.
     * @return Lista mutable de identificadores de cada personaje del equipo del usuario.
     * @throws NotFoundException
     * @see User
     * @see MutableList
     * @author Iván Vicente Morales
     */
    @Throws(NotFoundException::class)
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

    /**
     * Devuelve el equipo de un usuario como una lista mutable de identificadores de cada personaje.
     * @param email Email del usuario.
     * @return Lista mutable de identificadores de cada personaje del equipo del usuario.
     * @throws NotFoundException
     * @see User
     * @see MutableList
     * @author Iván Vicente Morales
     */
    @Throws(NotFoundException::class)
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
        conn = connect()

        return try {
            this.conn = connect()
            val user: User = this.getUser(user)!!

            Cipher.verifyPassword(pass, user.getSalt(), user.getPass())
        } catch(e: Exception) {
            false
        } finally {
            conn!!.close()
        }
    }

    override public fun checkLogin(email: Email, pass: String): Boolean {
        conn = connect()

        return try {
            this.conn = connect()
            val user: User = this.getUser(email)!!

            Cipher.verifyPassword(pass, user.getSalt(), user.getPass())
        } catch(e: Exception) {
            false
        } finally {
            conn!!.close()
        }
    }

    @Throws(SQLException::class)
    override public fun signUp(user: User, pass: String): ServerState {
        conn = connect()

        val salt: ByteArray = Cipher.generateSalt()

        return if(getUser(user.getUsername()) != null) {
            conn!!.close()
            ServerState.STATE_ERROR_USERNAME
        } else {
            return if(getUser(user.getEmail()) != null) {
                conn!!.close()
                ServerState.STATE_ERROR_EMAIL
            } else {
                val QUERY_INSERT_USER: String = "INSERT INTO User (username, email, password, salt, role) VALUES (\"${user.getUsername()}\", \"${user.getEmail()}\", ?, ?, \"${user.getRole()}\")"

                return try {
                    executeByteInsert(QUERY_INSERT_USER, Cipher.hashPassword(pass, salt), salt)
                    ServerState.STATE_SUCCESS
                } catch(e: SQLException) {
                    ServerState.STATE_ERROR_DATABASE
                } finally {
                    conn!!.close()
                }
            }
        }
    }

    @Throws(SQLException::class)
    override fun updateUserUsername(username: String, newUsername: String, pass: String): ServerState {
        conn = connect()
        val QUERY_UPDATE_USERNAME: String = "UPDATE User SET username = \"${newUsername}\" WHERE username = \"${username}\""

        return if(this.checkLogin(username, pass)) {
            return try {
                conn = connect()
                executeUpdate(QUERY_UPDATE_USERNAME)
                ServerState.STATE_SUCCESS
            } catch (e: SQLException) {
                ServerState.STATE_ERROR_DATABASE
            } finally {
                conn!!.close()
            }
        } else {
            conn!!.close()
            ServerState.STATE_ERROR_PASSWORD
        }
    }

    @Throws(SQLException::class)
    override fun updateUserPassword(username: String, oldPass: String, newPass: String): ServerState {
        conn = connect()
        val tempUser: User = this.getUser(username)!!
        val QUERY_UPDATE_PASSWORD: String = "UPDATE User SET password = \"${ByteHexConverter.bytesToHex(Cipher.hashPassword(newPass, tempUser.getSalt()))}\" WHERE username = \"${username}\""

        return if(this.checkLogin(username, oldPass)) {
            return try {
                conn = connect()
                executeUpdate(QUERY_UPDATE_PASSWORD)
                ServerState.STATE_SUCCESS
            } catch (e: SQLException) {
                ServerState.STATE_ERROR_DATABASE
            } finally {
                conn!!.close()
            }
        } else {
            conn!!.close()
            ServerState.STATE_ERROR_PASSWORD
        }
    }

    @Throws(SQLException::class)
    override fun updateUserPasswordForgotten(username: String, email: String, newPass: String): ServerState {
        conn = connect()
        val tempUser: User = this.getUser(username)!!
        val QUERY_UPDATE_PASSWORD_FORGOTTEN: String = "UPDATE User SET password = \"${ByteHexConverter.bytesToHex(Cipher.hashPassword(newPass, tempUser.getSalt()))}\" WHERE username = \"${username}\" AND email = \"${email}\""

        val ps: PreparedStatement = conn!!.prepareStatement(QUERY_UPDATE_PASSWORD_FORGOTTEN)

        return try {
            return when(ps.executeUpdate()) {
                0 -> ServerState.STATE_ERROR_USERNAME
                else -> ServerState.STATE_SUCCESS
            }
        } catch (e: SQLException) {
            ServerState.STATE_ERROR_DATABASE
        } finally {
            ps.close()
            conn!!.close()
        }
    }

    @Throws(SQLException::class)
    override fun updateUserEmail(user: User, pass: String): ServerState {
        conn = connect()
        val QUERY_UPDATE_EMAIL: String = "UPDATE User SET email = \"${user.getEmail()}\" WHERE username = \"${user.getUsername()}\""

        return if(this.checkLogin(user.getUsername(), pass)) {
            return try {
                conn = connect()
                executeUpdate(QUERY_UPDATE_EMAIL)
                ServerState.STATE_SUCCESS
            } catch (e: SQLException) {
                ServerState.STATE_ERROR_DATABASE
            } finally {
                conn!!.close()
            }
        } else {
            conn!!.close()
            ServerState.STATE_ERROR_PASSWORD
        }
    }

    override fun getSession(username: String, pass: String): User? {
        return if(this.checkLogin(username, pass)) {
            conn = connect()
            var user = getUser(username)
            conn!!.close()

            user
        } else {
            null
        }
    }

    override fun getSession(email: Email, pass: String): User? {
        return if(this.checkLogin(email, pass)) {
            conn = connect()
            var user = getUser(email)
            conn!!.close()

            user
        } else {
            null
        }
    }

    override fun getTeam(username: String): String? {
        conn = connect()
        var team = getUserTeam(username).joinToString(";")
        conn!!.close()

        return team
    }

    override fun getTeam(email: Email): String? {
        conn = connect()
        var team = getUserTeam(email).joinToString(";")
        conn!!.close()

        return team
    }

    override fun setTeam(username: String, team: List<Int>): ServerState {
        conn = connect()

        val QUERY_UPDATE_TEAMS: String = "UPDATE User SET teamElement1 = ${team[0]}, teamElement2 = ${team[1]}, teamElement3 = ${team[2]}, teamElement4 = ${team[3]}, teamElement5 = ${team[4]}, teamElement6 = ${team[5]}, teamElement7 = ${team[6]}, teamElement8 = ${team[7]} WHERE username = \"${username}\""

        return try {
            executeUpdate(QUERY_UPDATE_TEAMS)
            ServerState.STATE_SUCCESS
        } catch (e: SQLException) {
            ServerState.STATE_ERROR_DATABASE
        } finally {
            conn!!.close()
        }
    }

    override fun setTeam(email: Email, team: List<Int>): ServerState {
        conn = connect()

        val QUERY_UPDATE_TEAMS: String = "UPDATE User SET teamElement1 = ${team[0]}, teamElement2 = ${team[1]}, teamElement3 = ${team[2]}, teamElement4 = ${team[3]}, teamElement5 = ${team[4]}, teamElement6 = ${team[5]}, teamElement7 = ${team[6]}, teamElement8 = ${team[7]} WHERE email = \"${email.toString()}\""

        return try {
            executeUpdate(QUERY_UPDATE_TEAMS)
            ServerState.STATE_SUCCESS
        } catch (e: SQLException) {
            ServerState.STATE_ERROR_DATABASE
        } finally {
            conn!!.close()
        }
    }
}