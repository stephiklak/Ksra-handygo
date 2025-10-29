package com.ksra_handygo.network

import retrofit2.Call
import retrofit2.http.GET

data class CheckoutResponse(
    val success: Boolean,
    val message: String
)

interface CheckoutApi {
    @GET("api/users") // Replace with your actual backend path
    fun placeOrder(): Call<CheckoutResponse>
}
