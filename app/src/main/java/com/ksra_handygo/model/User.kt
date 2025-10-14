package com.ksra_handygo.model

data class User(
    val id: Long? = null,
    val name: String,
    val email: String,
    val passwordHash: String,
    val phone: String,
    val address: String,
    val role: Role
) {
    enum class Role {
        CUSTOMER,
        TECHNICIAN
    }
}
