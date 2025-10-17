package com.ksra_handygo.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ksra_handygo.R
import net.openid.appauth.*
import android.widget.Button



class AuthActivity : AppCompatActivity() {

    private lateinit var authService: AuthorizationService
    private var authRequest: AuthorizationRequest? = null

    private val AUTH_REQUEST_CODE = 101
    private val REGION = "us-east-2"
    private val USER_POOL_ID = "us-east-2_vafrricx7"  // Your Cognito User Pool ID
    private val CLIENT_ID = "7luss1jt3rqv467pqlrobur2o8"  // Your Cognito App Client ID
    private val REDIRECT_URI = Uri.parse("ksrafisherman://callback")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        authService = AuthorizationService(this)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        loginButton.setOnClickListener {
            Log.d("CognitoAuth", "Login button clicked.")
            fetchCognitoConfiguration()
        }
    }

    private fun fetchCognitoConfiguration() {
        val issuerUri = Uri.parse("https://cognito-idp.$REGION.amazonaws.com/$USER_POOL_ID")
        Log.d("CognitoAuth", "🔍 Fetching configuration from: $issuerUri")

        AuthorizationServiceConfiguration.fetchFromIssuer(issuerUri) { config, ex ->
            if (ex != null) {
                Log.e("CognitoAuth", "Failed to fetch config: ${ex.errorDescription}")
                ex.printStackTrace()
                return@fetchFromIssuer
            }
            if (config != null) {
                Log.d("CognitoAuth", "Config fetched successfully.")
                startLogin(config)
            } else {
                Log.e("CognitoAuth", "No configuration returned.")
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
                Log.d("CognitoAuth", "Authorization success. Exchanging token...")
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

                // TODO: Save tokens securely (e.g., SharedPreferences)
                // Then redirect to MainActivity
                startActivity(Intent(this, com.ksra_handygo.MainActivity::class.java))
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
