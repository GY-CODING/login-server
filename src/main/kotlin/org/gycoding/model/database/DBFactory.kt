package org.gycoding.model.database

class DBFactory {
    companion object {
        public val MODE_TEST: Int       = 0
        public val MODE_SQLITE: Int     = 1

        public fun getDAO(mode: Int): DBDAO? {
            return when(mode) {
                MODE_TEST -> null
                MODE_SQLITE -> SQLiteDAO()
                else -> null
            }
        }
    }
}