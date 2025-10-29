package com.ksra_handygo.services

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ksra_handygo.MainActivity
import com.ksra_handygo.auth.TokenStore

class ServicesFragment : Fragment() {

    private var hostActivity: MainActivity? = null
    private lateinit var tokenStore: TokenStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenStore = TokenStore(requireContext())
    }

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        hostActivity = activity as? MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        // Mock list heading
        layout.addView(TextView(requireContext()).apply {
            text = "Available Services (public)"
            textSize = 18f
            setPadding(0,0,0,20)
        })

        // Add a dummy list item
        val item = TextView(requireContext()).apply {
            text = "Small Fish — ₹200/kg  (tap to add)"
            setPadding(0,0,0,16)
        }
        layout.addView(item)

        // Checkout button
        val checkoutBtn = Button(requireContext()).apply {
            text = "Proceed to Checkout"
        }
        layout.addView(checkoutBtn)

        checkoutBtn.setOnClickListener {
            val token = tokenStore.getAccessToken()
            if (token == null) {
                // Soft gate dialog
                showLoginRequiredDialog()
            } else {
                // Logged in -> go to checkout
                hostActivity?.navigateToCheckout()
            }
        }

        return layout
    }

    private fun showLoginRequiredDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Login required")
            .setMessage("Please login or signup to proceed with checkout.")
            .setPositiveButton("Login") { _, _ ->
                // set pending destination to checkout and start auth
                hostActivity?.setPendingCheckoutAndStartAuth()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDetach() {
        super.onDetach()
        hostActivity = null
    }
}
