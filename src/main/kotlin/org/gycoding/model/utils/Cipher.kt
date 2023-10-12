package org.gycoding.model.utils

import java.security.SecureRandom
import java.security.MessageDigest

public object Cipher {
    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        val random = SecureRandom()
        random.nextBytes(salt)
        return salt
    }

    // Función para crear un hash de una contraseña dada y una sal.
    fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = md.digest(password.toByteArray())
        return hashedPassword
    }

    // Función para verificar una contraseña ingresada con el hash almacenado en la base de datos.
    fun verifyPassword(enteredPassword: String, salt: ByteArray, storedHashedPassword: ByteArray): Boolean {
        val calculatedHash = hashPassword(enteredPassword, salt)
        return calculatedHash.contentEquals(storedHashedPassword)
    }
}