package com.ksra_handygo.network

import com.ksra_handygo.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET("api/users")
    suspend fun getUsers(): List<User>

    @POST("api/users")
    suspend fun createUser(@Body user: User): User
}
