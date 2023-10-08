package org.gycoding.model.utils

import java.math.BigInteger
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.spec.InvalidKeySpecException

public object PBKDF2 {
    @get:Throws(NoSuchAlgorithmException::class)
    private val salt: ByteArray
        private get() {
            val sr: SecureRandom = SecureRandom.
            getInstance("SHA1PRNG")
            val salt = ByteArray(16)
            sr.nextBytes(salt)
            return salt
        }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    public fun processPassword(password: String): String {
        val iterations = 500
        val chars = password.toCharArray()
        val salt = salt
        val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = skf.generateSecret(spec).encoded
        return """
            Total iteration: $iterations
            Salt: ${toHex(salt)}
            Hash: ${toHex(hash)}
            """.trimIndent()
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun toHex(array: ByteArray): String {
        val bi = BigInteger(1, array)
        val hex = bi.toString(16)
        val paddingLength = array.size * 2 - hex.length
        return if (paddingLength > 0) {
            String.format("%0" + paddingLength + "d", 0) + hex
        } else {
            hex
        }
    }
}