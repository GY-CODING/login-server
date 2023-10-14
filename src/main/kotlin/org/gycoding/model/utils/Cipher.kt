package org.gycoding.model.utils

import java.security.SecureRandom
import java.security.MessageDigest

public object Cipher {
    /**
     * Generates a random salt.
     * @return The salt as an array of bytes.
     * @see ByteArray
     */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        val random = SecureRandom()
        random.nextBytes(salt)
        return salt
    }

    /**
     * Hashes a given password.
     * @param password Input password in plain text.
     * @param salt Random salt generated specifically for this hash generation.
     * @return The hashed password as an array of bytes.
     * @see ByteArray
     */
    fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedPassword = md.digest(password.toByteArray())
        return hashedPassword
    }

    /**
     * Evaluates a given password with a hashed function, conditioned by a salt.
     * @param enteredPassword Input password in plain text.
     * @param salt
     * @param storedHashedPassword A hashed password as an array of bytes.
     * @return The evaluation of passwords.
     * @see ByteArray
     */
    fun verifyPassword(enteredPassword: String, salt: ByteArray, storedHashedPassword: ByteArray): Boolean {
        val calculatedHash = hashPassword(enteredPassword, salt)
        return calculatedHash.contentEquals(storedHashedPassword)
    }
}