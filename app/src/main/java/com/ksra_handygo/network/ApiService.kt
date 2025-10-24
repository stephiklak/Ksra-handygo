package com.ksra_handygo.network

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("api/users")
    suspend fun testAuth(): Response<String>
}
