package com.ksra_handygo.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.*

class AuthRedirectHandlerActivity : AppCompatActivity() {

    private lateinit var authService: AuthorizationService

    private val clientId = "7luss1jt3rqv467pqlrobur2o8"
    private val redirectUri = Uri.parse("ksrafisherman://callback")
    private val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse("https://us-east-2vafrricx7.auth.us-east-2.amazoncognito.com/oauth2/authorize"),
        Uri.parse("https://us-east-2vafrricx7.auth.us-east-2.amazoncognito.com/oauth2/token")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthorizationService(this)

        val data: Uri? = intent?.data
        if (data != null) {
            val response = AuthorizationResponse.fromIntent(intent)
            val ex = AuthorizationException.fromIntent(intent)

            if (response != null) {
                Log.d("CognitoAuth", "Authorization code received: ${response.authorizationCode}")

                val tokenRequest = response.createTokenExchangeRequest()
                authService.performTokenRequest(tokenRequest) { tokenResp, tokenEx ->
                    if (tokenResp != null) {
                        Log.d("CognitoAuth", "Login successful!")
                        val resultIntent = Intent().apply {
                            putExtra("success", true)
                            putExtra("idToken", tokenResp.idToken)
                            putExtra("accessToken", tokenResp.accessToken)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Log.e("CognitoAuth", "Token exchange failed: ${tokenEx?.errorDescription}")
                        val resultIntent = Intent().apply {
                            putExtra("success", false)
                            putExtra("error", tokenEx?.errorDescription)
                        }
                        setResult(RESULT_CANCELED, resultIntent)
                        finish()
                    }
                }
            } else {
                Log.e("CognitoAuth", "Authorization failed: ${ex?.errorDescription}")
                val resultIntent = Intent().apply {
                    putExtra("success", false)
                    putExtra("error", ex?.errorDescription)
                }
                setResult(RESULT_CANCELED, resultIntent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }
}
