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
 * Objeto de acceso a datos de la base de datos en AWS RDS con Planetscale de MySQL.
 * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
 * @see <a href="https://aws.amazon.com/es/rds/">Amazon Web Services RDS</a>
 * @see <a href="https://planetscale">Planetscale</a>
 */
class MySQLDAO() : DBDAO {
    private var conn: Connection? = null

    init {
        Class.forName("com.mysql.cj.jdbc.Driver")
    }



    /* ================# PRIVATE FUNCTIONS #================ */

    /**
     * Conecta a una base de datos.
     * @return Instancia de la conexión.
     * @see Connection
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
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
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
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
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
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
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
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
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
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
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
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
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
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
}