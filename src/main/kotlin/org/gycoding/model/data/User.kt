package org.gycoding.model.data

data class User (
    private var username: String,
    private var email: Email,
    private var pass: String,
) {
    fun getUsername(): String {
        return this.username
    }
    fun setUsername(username: String) {
        this.username = username
    }

    fun getEmail(): Email {
        return this.email
    }
    fun setEmail(email: Email) {
        this.email = email
    }

    override fun toString(): String {
        return "Username: ${this.getUsername()}\nEmail: ${this.getEmail()}\n"
    }

    fun toJSON(): String {
        return "{\"username\": \"${this.getUsername()}\",\"email\": \"${this.getEmail()}\"}"
    }
}