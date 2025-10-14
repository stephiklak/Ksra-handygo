package com.ksra_handygo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.ksra_handygo.ui.UserScreen
import com.ksra_handygo.ui.theme.KsrahandygoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KsrahandygoTheme {
                // Display your main Compose screen
                UserScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
