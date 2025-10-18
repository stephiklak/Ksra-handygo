package com.ksra_handygo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ksra_handygo.auth.AuthManager

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager
    private val AUTH_REQ_CODE = 1001
    private val END_SESSION_REQ = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btnLogin = Button(this).apply { text = "Login with AWS Cognito" }
        val btnLogout = Button(this).apply { text = "Logout" }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(80, 200, 80, 0)
            addView(btnLogin)
            addView(btnLogout)
        }
        setContentView(layout)

        authManager = AuthManager(this)

        btnLogin.setOnClickListener {
            Log.d("CognitoAuth", "Login button clicked → opening Cognito Hosted UI")
            val authIntent = authManager.createAuthIntent()
            startActivityForResult(authIntent, AUTH_REQ_CODE)
        }

        btnLogout.setOnClickListener {
            Log.d("CognitoAuth", "Logout button clicked → redirecting to logout endpoint")
            val endIntent = authManager.createEndSessionIntent()
            if (endIntent != null) startActivityForResult(endIntent, END_SESSION_REQ)
            else Toast.makeText(this, "Logout not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AUTH_REQ_CODE -> {
                if (resultCode == RESULT_OK && data != null) {
                    val idToken = data.getStringExtra("idToken")
                    val accessToken = data.getStringExtra("accessToken")
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_LONG).show()
                    Log.d("CognitoAuth", "ID Token: ${idToken?.take(20)}...")
                    Log.d("CognitoAuth", "Access Token: ${accessToken?.take(20)}...")
                } else {
                    val error = data?.getStringExtra("error") ?: "Unknown error"
                    Toast.makeText(this, "Login failed: $error", Toast.LENGTH_LONG).show()
                    Log.e("CognitoAuth", "Login failed: $error")
                }
            }

            END_SESSION_REQ -> {
                Log.d("CognitoAuth", "Logout completed, user redirected back.")
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authManager.dispose()
    }
}
