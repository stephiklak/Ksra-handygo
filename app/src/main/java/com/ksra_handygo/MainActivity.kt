package com.ksra_handygo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ksra_handygo.auth.AuthActivity
import com.ksra_handygo.auth.TokenStore

class MainActivity : AppCompatActivity() {

    private lateinit var tokenStore: TokenStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenStore = TokenStore(this)

        val btnLogin = Button(this).apply { text = "Login with AWS Cognito" }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(btnLogin)
        }
        setContentView(layout)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }
}
