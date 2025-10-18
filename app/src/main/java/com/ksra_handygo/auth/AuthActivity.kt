package com.ksra_handygo.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ksra_handygo.MainActivity
import net.openid.appauth.*

class AuthActivity : AppCompatActivity() {

    private lateinit var authService: AuthorizationService
    private var authRequest: AuthorizationRequest? = null

    private val AUTH_REQUEST_CODE = 101

    // Your Cognito details
    private val REGION = "us-east-2"
    private val USER_POOL_ID = "us-east-2_vafrricx7"
    private val CLIENT_ID = "7luss1jt3rqv467pqlrobur2o8"
    private val REDIRECT_URI = Uri.parse("ksrafisherman://callback")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthorizationService(this)

        Log.d("CognitoAuth", "App started, fetching Cognito config...")
        fetchCognitoConfiguration()
    }

    private fun fetchCognitoConfiguration() {
        val issuerUri = Uri.parse("https://cognito-idp.$REGION.amazonaws.com/$USER_POOL_ID")
        AuthorizationServiceConfiguration.fetchFromIssuer(issuerUri) { config, ex ->
            if (ex != null) {
                Log.e("CognitoAuth", "Failed to fetch Cognito config: ${ex.errorDescription}")
                return@fetchFromIssuer
            }
            if (config != null) {
                Log.d("CognitoAuth", "Cognito config fetched successfully.")
                startLogin(config)
            }
        }
    }

    private fun startLogin(serviceConfig: AuthorizationServiceConfiguration) {
        authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            REDIRECT_URI
        )
            .setScope("openid email profile")
            .build()

        val authIntent = authService.getAuthorizationRequestIntent(authRequest!!)
        startActivityForResult(authIntent, AUTH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTH_REQUEST_CODE && data != null) {
            val response = AuthorizationResponse.fromIntent(data)
            val ex = AuthorizationException.fromIntent(data)
            if (response != null) {
                Log.d("CognitoAuth", "Authorization success, exchanging code for token...")
                exchangeCodeForToken(response)
            } else {
                Log.e("CognitoAuth", "Authorization failed: ${ex?.errorDescription}")
            }
        }
    }

    private fun exchangeCodeForToken(response: AuthorizationResponse) {
        authService.performTokenRequest(
            response.createTokenExchangeRequest()
        ) { tokenResponse, ex ->
            if (tokenResponse != null) {
                Log.d("CognitoAuth", "Access Token: ${tokenResponse.accessToken}")
                Log.d("CognitoAuth", "ID Token: ${tokenResponse.idToken}")

                // You can save tokens securely (e.g., SharedPreferences)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Log.e("CognitoAuth", "Token exchange failed: ${ex?.errorDescription}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }
}
