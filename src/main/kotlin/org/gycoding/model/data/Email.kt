package org.gycoding.model.data

import java.util.*

/**
 * Representación de un correo electrónico.
 * @param strigifiedEmail Email como cadena de caracteres entera, sin separar identificador de servicio.
 * @author Iván Vicente Morales (<a href="https://github.com/srtoxyc">@srtoxyc</a>)
 */
class Email(
    private var stringifiedEmail: String,
) {
    companion object {
        public val REGEX: Regex         = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
        private val MAIL_DELIM: String  = "@"
    }

    private var identifier: String      = ""
    private var service: String         = ""

    init {
        val mailElements: List<String>  = processMail(this.stringifiedEmail)

        this.setIdentifier(mailElements[0])
        this.setService(mailElements[1])
    }

    public fun getIdentifier(): String {
        return this.identifier
    }
    public fun setIdentifier(identifier: String) {
        this.identifier = identifier
    }

    public fun getService(): String {
        return this.service
    }
    public fun setService(service: String) {
        this.service = service
    }

    private fun processMail(mail: String): List<String> {
        val st                                  = StringTokenizer(mail, MAIL_DELIM)
        val mailElements: MutableList<String>   = ArrayList()

        while (st.hasMoreTokens()) {
            mailElements.add(st.nextToken())
        }

        return mailElements
    }

    override fun toString(): String {
        return "${this.identifier}@${this.service}"
    }
}