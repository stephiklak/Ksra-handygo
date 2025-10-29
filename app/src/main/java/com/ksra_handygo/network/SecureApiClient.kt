package com.ksra_handygo.network

import android.content.Context
import com.ksra_handygo.auth.TokenStore
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object SecureApiClient {

    private const val BASE_URL = "https://98.81.162.163:8443/"

    // Retrofit builder with SSL and token interceptor
    fun create(context: Context): Retrofit {
        val tokenStore = TokenStore(context)

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = tokenStore.getAccessToken()
            val newRequest = if (!token.isNullOrEmpty()) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                original
            }
            chain.proceed(newRequest)
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val client = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Checkout API call
    fun callCheckout(context: Context, callback: (success: Boolean, body: String?) -> Unit) {
        val tokenStore = TokenStore(context)
        val token = tokenStore.getAccessToken()
        if (token.isNullOrEmpty()) {
            callback(false, "Token missing")
            return
        }

        val api = create(context).create(CheckoutApi::class.java)
        api.placeOrder().enqueue(object : Callback<CheckoutResponse> {
            override fun onResponse(
                call: Call<CheckoutResponse>,
                response: Response<CheckoutResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body()!!.success, response.body()!!.message)
                } else {
                    callback(false, "Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                callback(false, t.localizedMessage)
            }
        })
    }
}
