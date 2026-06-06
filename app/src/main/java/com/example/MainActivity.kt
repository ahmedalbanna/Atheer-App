package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.VideoViewModel
import com.example.ui.screens.AtheerAppContent
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: VideoViewModel = viewModel()
                
                // Intercept hardware or gesture back button to navigate tabs backward
                BackHandler {
                    val handled = viewModel.handleBackPress()
                    if (!handled) {
                        finish()
                    }
                }
                
                AtheerAppContent(viewModel = viewModel)
            }
        }
    }
}

