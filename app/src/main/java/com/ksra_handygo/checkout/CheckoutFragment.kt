package com.ksra_handygo.checkout

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ksra_handygo.network.SecureApiClient

class CheckoutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
        }

        layout.addView(TextView(requireContext()).apply {
            text = "Checkout"
            textSize = 20f
            setPadding(0, 0, 0, 16)
        })

        val payBtn = Button(requireContext()).apply { text = "Place Order" }
        layout.addView(payBtn)

        payBtn.setOnClickListener {
            SecureApiClient.callCheckout(requireContext()) { success, body ->
                activity?.runOnUiThread {
                    if (success) {
                        Toast.makeText(requireContext(), "Order placed", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Checkout failed: $body", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return layout
    }
}
