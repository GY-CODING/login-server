package org.gycoding.model.utils

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Singleton de cifrado de cadenas de caracteres. Utiliza una sal (valor cifrado de aleatorización del cifrado de la clave) asociada a un usuario para generar un cifrado de clave único para cada usuario.
 * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
 */
object Cipher {
    /**
     * Genera una sal aleatoria.
     * @return Sal en formato array de bytes.
     * @see ByteArray
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        val random = SecureRandom()
        random.nextBytes(salt)
        return salt
    }

    /**
     * Cifra una contraseña con una función hash SHA-256 y la sal generada y asociada al usuario.
     * @param password Contraseña del usuario.
     * @param salt Sal del usuario.
     * @return Contraseña ya cifrada en formato array de bytes.
     * @see ByteArray
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
     */
    fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = md.digest(password.toByteArray())
        return hashedPassword
    }

    /**
     * Evalúa la equidad de una contraseña de entrada, frente a la contraseña de un usuario, junto a su sal asociada.
     * @param enteredPassword Contraseña de entrada
     * @param salt Sal del usuario en formato array de bytes.
     * @param storedHashedPassword Contraseña del usuario en formato array de bytes.
     * @return Evaluación de la equidad.
     * @see ByteArray
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
     */
    fun verifyPassword(enteredPassword: String, salt: ByteArray, storedHashedPassword: ByteArray): Boolean {
        val calculatedHash = hashPassword(enteredPassword, salt)
        return calculatedHash.contentEquals(storedHashedPassword)
    }
}