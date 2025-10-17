package com.ksra_handygo

import android.content.Context
import android.content.Intent
import android.net.Uri
import net.openid.appauth.*

class AuthManager(private val context: Context) {

    private val authService: AuthorizationService by lazy { AuthorizationService(context) }
    private var authState: AuthState? = null

    /**
     * Start authorization flow. The caller should launch the returned Intent (use startActivityForResult).
     * Returns the intent to start the browser.
     */
    fun createAuthIntent(): Intent {
        // Try discovery (recommended). If discovery fails, you can use manual endpoints.
        val serviceConfig = AuthorizationServiceConfiguration.fetchFromIssuer(
            Uri.parse(AuthConfig.ISSUER),
            { fetchedConfig, ex ->
                if (fetchedConfig != null) {
                    // Save fetched config so we can build requests from it later
                    authState = AuthState(fetchedConfig)
                } else {
                    ex?.printStackTrace()
                }
            }
        )

        // NOTE: fetchFromIssuer is async. We'll build a request using manual endpoints immediately
        // to avoid race conditions. If discovery succeeded quickly you can replace with fetchedConfig.
        val manualConfig = AuthorizationServiceConfiguration(
            // authorize endpoint
            Uri.parse("${AuthConfig.ISSUER}/oauth2/authorize"),
            // token endpoint
            Uri.parse("${AuthConfig.ISSUER}/oauth2/token")
        )

        val authRequest = AuthorizationRequest.Builder(
            manualConfig,
            AuthConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(AuthConfig.REDIRECT_URI)
        ).setScope("openid email profile").build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    /**
     * Call this in onActivityResult after returning from authorization activity.
     * Caller must pass data intent and will receive tokens in the callback.
     */
    fun handleAuthResponse(data: Intent?, callback: (success: Boolean, idToken: String?, accessToken: String?, error: String?) -> Unit) {
        val resp = AuthorizationResponse.fromIntent(data!!)
        val ex = AuthorizationException.fromIntent(data)
        if (resp != null) {
            // Exchange code for tokens
            authService.performTokenRequest(
                resp.createTokenExchangeRequest()
            ) { tokenResponse, tokenEx ->
                if (tokenResponse != null) {
                    authState = AuthState(resp, tokenResponse, tokenEx)
                    callback(true, tokenResponse.idToken, tokenResponse.accessToken, null)
                } else {
                    tokenEx?.printStackTrace()
                    callback(false, null, null, tokenEx?.errorDescription ?: tokenEx?.localizedMessage)
                }
            }
        } else {
            callback(false, null, null, ex?.errorDescription ?: ex?.localizedMessage)
        }
    }

    /**
     * Build and return an Intent to perform logout (end session).
     * Caller should startActivityForResult with this intent.
     */
    fun createEndSessionIntent(): Intent? {
        // End session endpoint for Cognito hosted logout:
        val endSessionEndpoint = Uri.parse("${AuthConfig.ISSUER}/oauth2/logout")
        val endSessionRequest = EndSessionRequest.Builder(
            AuthorizationServiceConfiguration(
                // not used for token, but EndSessionRequest needs a config
                Uri.parse("${AuthConfig.ISSUER}/oauth2/authorize"),
                Uri.parse("${AuthConfig.ISSUER}/oauth2/token")
            )
        )
            .setPostLogoutRedirectUri(Uri.parse(AuthConfig.LOGOUT_REDIRECT_URI))
            .setIdTokenHint(authState?.idToken)
            .setAdditionalParameters(mapOf("client_id" to AuthConfig.CLIENT_ID))
            .build()

        return authService.getEndSessionRequestIntent(endSessionRequest)
    }

    fun dispose() { authService.dispose() }
}
