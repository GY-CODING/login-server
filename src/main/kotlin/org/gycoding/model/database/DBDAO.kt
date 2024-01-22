package org.gycoding.model.database

import org.gycoding.model.data.Email
import org.gycoding.model.data.ServerState
import org.gycoding.model.data.User
import java.sql.SQLException

/**
 * Interfaz de objeto de acceso a una base de datos.
 * @author Iván Vicente Morales
 */
interface DBDAO {
    /**
     * Evalúa si el usuario existe en la base de datos y si los parámetros de login son correctos, si es así, permite el acceso a la cuenta.
     * @param username Nombre del usuario.
     * @param pass
     * @return Si el usuario puede o no puede acceder a la cuenta. No dará acceso si los parámetros de login son incorrectos o si ocurre un error en la comunicación con la base de datos.
     * @author Iván Vicente Morales
     */
    fun checkLogin(username: String, pass: String): Boolean

    /**
     * Evalúa si el usuario existe en la base de datos y si los parámetros de login son correctos, si es así, permite el acceso a la cuenta.
     * @param email Email del usuario (cualquier servicio está permitido).
     * @param pass
     * @return Si el usuario puede o no puede acceder a la cuenta. No dará acceso si los parámetros de login son incorrectos o si ocurre un error en la comunicación con la base de datos.
     * @author Iván Vicente Morales
     */
    fun checkLogin(email: Email, pass: String): Boolean

    /**
     * Registra un usuario.
     * @param user
     * @param pass
     * @return Estado del registro del usuario.
     * @throws SQLException
     * @see ByteArray
     * @author Iván Vicente Morales
     */
    fun signUp(user: User, pass: String): ServerState

    /**
     * Modifica el nombre del usuario.
     * @param username Anterior nombre del usuario.
     * @param newUsername Nuevo nombre del usuario.
     * @param pass
     * @return Estado de la modificación del nombre del usuario.
     * @throws SQLException
     * @author Iván Vicente Morales
     */
    fun updateUserUsername(username: String, newUsername: String, pass: String): ServerState

    /**
     * Modifica la contraseña del usuario.
     * @param username Nombre del usuario.
     * @param oldPass Anterior contraseña del usuario.
     * @param newPass Nueva contraseña del usuario.
     * @return Estado de la modificación de la contraseña del usuario.
     * @throws SQLException
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
     */
    fun updateUserPassword(username: String, oldPass: String, newPass: String): ServerState

    /**
     * Modifica la contraseña del usuario si esta ha sido olvidada.
     * @param username Nombre del usuario.
     * @param email Email del usuario.
     * @param newPass Nueva contraseña del usuario.
     * @return Estado de la modificación de la contraseña del usuario.
     * @throws SQLException
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
     */
    fun updateUserPasswordForgotten(username: String, email: String, newPass: String): ServerState

    /**
     * Modifica el email del usuario.
     * @param user
     * @param pass
     * @return Estado de la modificación del email.
     * @throws SQLException
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
     */
    fun updateUserEmail(user: User, pass: String): ServerState
}