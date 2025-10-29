package com.ksra_handygo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ksra_handygo.services.ServicesFragment
import com.ksra_handygo.auth.LoginFragment
import com.ksra_handygo.checkout.CheckoutFragment

enum class PendingDestination { NONE, CHECKOUT }

class MainActivity : AppCompatActivity() {

    private var pendingDestination: PendingDestination = PendingDestination.NONE

    // Register a launcher to start your existing AuthActivity (Cognito AppAuth)
    private val authLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Successful login — notify current fragment / route to pending destination
            onAuthSuccess()
        } else {
            // login cancelled or failed
            onAuthCancelled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start with LoginFragment (with Skip option)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

    fun startAuthFlow() {
        // Start your existing AuthActivity which exchanges token and returns RESULT_OK on success
        val i = Intent(this, com.ksra_handygo.auth.AuthActivity::class.java)
        authLauncher.launch(i)
    }

    private fun onAuthSuccess() {
        // If user wanted to go to checkout, do that. Otherwise show services.
        when (pendingDestination) {
            PendingDestination.CHECKOUT -> {
                // clear pending and navigate to checkout
                pendingDestination = PendingDestination.NONE
                navigateToCheckout()
            }
            PendingDestination.NONE -> {
                // By default go to Services
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ServicesFragment())
                    .commit()
            }
        }
    }

    private fun onAuthCancelled() {
        // Stay where you are. Optionally show a toast or message.
    }

    fun setPendingCheckoutAndStartAuth() {
        pendingDestination = PendingDestination.CHECKOUT
        startAuthFlow()
    }

    fun navigateToServices() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ServicesFragment())
            .commit()
    }

    fun navigateToCheckout() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CheckoutFragment())
            .commit()
    }
}
