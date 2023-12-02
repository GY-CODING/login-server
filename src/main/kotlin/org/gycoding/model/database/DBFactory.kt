package org.gycoding.model.database

/**
 * Singleton que controla las instancias de objetos de acceso a datos.
 * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
 */
object DBFactory {
    public val MODE_TEST: Int       = 0
    public val MODE_MYSQL: Int      = 1

    /**
     * Devuelve una instancia de un objeto de acceso a datos dependiendo del modo que se le proporcione.
     * @param mode Modo de la factoría.
     * @return Instancia de un objeto de acceso a datos.
     * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
     */
    public fun getDAO(mode: Int): DBDAO? {
        return when(mode) {
            MODE_TEST -> null
            MODE_MYSQL -> MySQLDAO()
            else -> null
        }
    }
}