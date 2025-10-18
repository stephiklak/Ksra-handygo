package com.ksra_handygo.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import net.openid.appauth.*

class AuthManager(private val context: Context) {

    private val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse("https://us-east-2vafrricx7.auth.us-east-2.amazoncognito.com/oauth2/authorize"),
        Uri.parse("https://us-east-2vafrricx7.auth.us-east-2.amazoncognito.com/oauth2/token"),
        null,
        Uri.parse("https://us-east-2vafrricx7.auth.us-east-2.amazoncognito.com/logout")
    )

    private val clientId = "7luss1jt3rqv467pqlrobur2o8"
    private val redirectUri = Uri.parse("ksrafisherman://callback")
    private val scope = "openid profile email"

    private val authService = AuthorizationService(context)

    fun createAuthIntent(): Intent {
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        )
            .setScopes(scope)
            .build()

        val fullUrl = authRequest.toUri().toString()
        Log.d("AuthManager", "Cognito login URL: $fullUrl")

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    fun createEndSessionIntent(): Intent? {
        val logoutUri = Uri.parse("https://us-east-2vafrricx7.auth.us-east-2.amazoncognito.com/logout")
            .buildUpon()
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("logout_uri", redirectUri.toString())
            .build()
        return Intent(Intent.ACTION_VIEW, logoutUri)
    }

    fun dispose() {
        authService.dispose()
    }
}
