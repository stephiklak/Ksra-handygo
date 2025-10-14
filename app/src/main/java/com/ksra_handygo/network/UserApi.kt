package com.ksra_handygo.network

import com.ksra_handygo.model.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {

    @GET("/api/users")
    suspend fun getUsers(): List<User>

    @POST("/api/users")
    suspend fun createUser(@Body user: User): User

    companion object {
        private const val BASE_URL = "http://98.81.162.163:8080"

        fun create(): UserApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserApi::class.java)
        }
    }
}
