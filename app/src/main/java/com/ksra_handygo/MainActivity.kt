package com.ksra_handygo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private val AUTH_REQ_CODE = 1001
    private val END_SESSION_REQ = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // simple layout with two buttons - you can replace with Compose/View as needed
        val btnLogin = Button(this).apply { text = "Login (Cognito)" }
        val btnLogout = Button(this).apply { text = "Logout" }
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            addView(btnLogin)
            addView(btnLogout)
        }
        setContentView(layout)

        authManager = AuthManager(this)

        btnLogin.setOnClickListener {
            val authIntent = authManager.createAuthIntent()
            startActivityForResult(authIntent, AUTH_REQ_CODE)
        }

        btnLogout.setOnClickListener {
            val endIntent = authManager.createEndSessionIntent()
            if (endIntent != null) startActivityForResult(endIntent, END_SESSION_REQ)
            else Toast.makeText(this, "Logout not available", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("onActivityResult for AppAuth", level = DeprecationLevel.WARNING)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AUTH_REQ_CODE -> {
                authManager.handleAuthResponse(data) { success, idToken, accessToken, error ->
                    if (success) {
                        Toast.makeText(this, "Login success\nID token len=${idToken?.length}", Toast.LENGTH_LONG).show()
                        // Save tokens (securely) and call backend with accessToken if needed
                    } else {
                        Toast.makeText(this, "Login failed: $error", Toast.LENGTH_LONG).show()
                    }
                }
            }
            END_SESSION_REQ -> {
                Toast.makeText(this, "Logged out (returned)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authManager.dispose()
    }
}
