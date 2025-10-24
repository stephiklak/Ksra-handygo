package com.ksra_handygo.auth

import android.content.Context
import android.content.SharedPreferences

class TokenStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String?, idToken: String?) {
        prefs.edit().apply {
            putString("access_token", accessToken)
            putString("id_token", idToken)
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString("access_token", null)
    fun getIdToken(): String? = prefs.getString("id_token", null)
    fun clear() = prefs.edit().clear().apply()
}
