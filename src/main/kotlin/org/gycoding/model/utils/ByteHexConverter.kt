package org.gycoding.model.utils

object ByteHexConverter {
    /**
     * Converts an array of bytes to a stringified hexadecimal number.
     * @param bytes Array of bytes to be converted.
     * @return Stringified hexadecimal number.
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
     * Converts a stringified hexadecimal number to an array of bytes.
     * @param hex Array of bytes to be converted.
     * @return Array of bytes.
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