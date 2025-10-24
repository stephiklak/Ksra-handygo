package com.ksra_handygo.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.*

class AuthActivity : AppCompatActivity() {

    private lateinit var authService: AuthorizationService
    private var authRequest: AuthorizationRequest? = null
    private var authConfig: AuthorizationServiceConfiguration? = null

    private val CLIENT_ID = "7luss1jt3rqv467pqlrobur2o8"
    private val REDIRECT_URI = Uri.parse("ksrafisherman://callback")
    private val SCOPES = "email openid phone"

    private val AUTH_REQ_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authService = AuthorizationService(this)
        fetchCognitoConfiguration()
    }

    private fun fetchCognitoConfiguration() {
        val issuerUri = Uri.parse("https://cognito-idp.us-east-2.amazonaws.com/us-east-2_vafrricx7")
        AuthorizationServiceConfiguration.fetchFromIssuer(issuerUri) { config, ex ->
            if (ex != null) {
                Log.e("CognitoAuth", "Failed to fetch configuration: ${ex.errorDescription}")
                Toast.makeText(this, "Configuration fetch failed", Toast.LENGTH_LONG).show()
                return@fetchFromIssuer
            }
            if (config != null) {
                authConfig = config
                startLogin()
            }
        }
    }

    private fun startLogin() {
        if (authConfig == null) return

        // Generate PKCE code verifier & challenge
        val codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier()
        val codeChallenge = CodeVerifierUtil.deriveCodeVerifierChallenge(codeVerifier)

        authRequest = AuthorizationRequest.Builder(
            authConfig!!,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            REDIRECT_URI
        )
            .setScope(SCOPES)
            .setCodeVerifier(codeVerifier, codeChallenge, "S256")
            .build()

        val authIntent = authService.getAuthorizationRequestIntent(authRequest!!)
        startActivityForResult(authIntent, AUTH_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTH_REQ_CODE && data != null) {
            val response = AuthorizationResponse.fromIntent(data)
            val ex = AuthorizationException.fromIntent(data)
            if (response != null) {
                Log.d("CognitoAuth", "Auth code received: ${response.authorizationCode}")
                exchangeToken(response)
            } else {
                Log.e("CognitoAuth", "Auth failed: ${ex?.errorDescription}")
                Toast.makeText(this, "Login failed: ${ex?.errorDescription}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun exchangeToken(response: AuthorizationResponse) {
        authService.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, ex ->
            if (tokenResponse != null) {
                Log.d("CognitoAuth", "Access Token: ${tokenResponse.accessToken}")
                Log.d("CognitoAuth", "ID Token: ${tokenResponse.idToken}")

                // Save tokens
                val tokenStore = TokenStore(this)
                tokenStore.saveTokens(tokenResponse.accessToken, tokenResponse.idToken)

                Toast.makeText(this, "Login successful!", Toast.LENGTH_LONG).show()

                // Proceed to MainActivity
                startActivity(Intent(this, com.ksra_handygo.MainActivity::class.java))
                finish()
            } else {
                Log.e("CognitoAuth", "Token exchange failed: ${ex?.errorDescription}")
                Toast.makeText(this, "Token exchange failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }
}
