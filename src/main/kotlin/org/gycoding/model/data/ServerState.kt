package org.gycoding.model.data

/**
 * Enumeración de los estados posibles que puede devolver el servidor.
 * @param value Valor como número entero.
 * @author Iván Vicente Morales
 */
enum class ServerState(val value: Int) {
    STATE_ERROR_USERNAME(-3),
    STATE_ERROR_EMAIL(-2),
    STATE_ERROR_PASSWORD(-1),
    STATE_ERROR_DATABASE(0),
    STATE_SUCCESS(1)
}