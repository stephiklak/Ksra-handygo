package com.ksra_handygo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ksra_handygo.auth.AuthActivity
import com.ksra_handygo.auth.TokenStore

class MainActivity : AppCompatActivity() {

    private lateinit var tokenStore: TokenStore
    private lateinit var btnLogin: Button
    private lateinit var tvResponse: TextView

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokenStore = TokenStore(this)
        btnLogin = findViewById(R.id.btnLogin)
        tvResponse = findViewById(R.id.tvResponse)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        // Observe ViewModel LiveData
        viewModel.userResponse.observe(this, Observer { response ->
            tvResponse.text = response // display API data here
        })

        // Once token is available, call backend
        val token = tokenStore.getAccessToken()
        if (!token.isNullOrEmpty()) {
            viewModel.fetchUsers(token)
        }
    }
}
