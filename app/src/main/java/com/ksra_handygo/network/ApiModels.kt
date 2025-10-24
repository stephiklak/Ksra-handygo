package com.ksra_handygo.network

data class UserDto(
    val id: Int,
    val name: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val role: String?
)
