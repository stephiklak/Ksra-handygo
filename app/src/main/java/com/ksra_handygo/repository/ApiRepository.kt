package com.ksra_handygo.repository

import android.content.Context
import com.ksra_handygo.network.ApiClient
import com.ksra_handygo.network.ApiService

class ApiRepository(context: Context) {
    private val api = ApiClient.create(context).create(ApiService::class.java)

    suspend fun testBackend(): String {
        val response = api.testAuth()
        return if (response.isSuccessful) {
            response.body() ?: "Empty response"
        } else {
            "Error: ${response.code()} - ${response.message()}"
        }
    }
}
