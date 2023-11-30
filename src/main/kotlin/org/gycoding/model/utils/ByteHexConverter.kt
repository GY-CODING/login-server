package org.gycoding.model.utils

/**
 * Conversor bidireccional de bytes y cadenas hexadecimales.
 * @author Iván Vicente Morales
 */
object ByteHexConverter {
    /**
     * Convierte un array de bytes a una cadena hexadecimal.
     * @param bytes Array de bytes.
     * @return Cadena hexadecimal.
     * @author Iván Vicente Morales
     */
    public fun bytesToHex(bytes: ByteArray): String {
        val stringBuilder = StringBuilder()
        for (byte in bytes) {
            stringBuilder.append(String.format("%02x", byte))
        }
        return stringBuilder.toString()
    }

    /**
     * Convierte una cadena hexadecimal en un array de bytes.
     * @param hex Cadena hexadecimal.
     * @return Array de bytes.
     * @author Iván Vicente Morales.
     */
    public fun hexToBytes(hex: String): ByteArray {
        val len = hex.length
        val result = ByteArray(len / 2)

        for (i in 0 until len step 2) {
            val byte = (Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)
            result[i / 2] = byte.toByte()
        }

        return result
    }
}