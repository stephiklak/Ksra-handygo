package com.ksra_handygo.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ksra_handygo.MainActivity
import com.ksra_handygo.services.ServicesFragment

class LoginFragment : Fragment() {

    private var hostActivity: MainActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        hostActivity = activity as? MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Simple programmatic UI — you can replace with XML if you want
        val root = inflater.inflate(android.R.layout.simple_list_item_1, container, false)

        // We'll create a small layout programmatically to keep it minimal
        val layout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 80, 40, 40)
        }

        val loginBtn = Button(requireContext()).apply { text = "Login / Signup" }
        val skipBtn = Button(requireContext()).apply { text = "Skip for now" }

        layout.addView(loginBtn)
        layout.addView(skipBtn)

        loginBtn.setOnClickListener {
            // Launch AuthActivity via MainActivity
            hostActivity?.startAuthFlow()
        }

        skipBtn.setOnClickListener {
            // Navigate to services
            hostActivity?.navigateToServices()
        }

        return layout
    }

    override fun onDetach() {
        super.onDetach()
        hostActivity = null
    }
}
