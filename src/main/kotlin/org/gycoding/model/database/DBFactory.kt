package org.gycoding.model.database

object DBFactory {
    public val MODE_TEST: Int       = 0
    public val MODE_SQLITE: Int     = 1
    public val MODE_MYSQL: Int      = 2

    public fun getDAO(mode: Int): DBDAO? {
        return when(mode) {
            MODE_TEST -> null
            MODE_SQLITE -> SQLiteDAO()
            MODE_MYSQL -> MySQLDAO()
            else -> null
        }
    }
}