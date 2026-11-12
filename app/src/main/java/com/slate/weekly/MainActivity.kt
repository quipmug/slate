package com.slate.weekly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.slate.weekly.ui.SlateRoot
import com.slate.weekly.ui.SlateViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SlateViewModel by viewModels {
        SlateViewModel.factory((application as SlateApp).store)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlateRoot(viewModel)
        }
    }
}
