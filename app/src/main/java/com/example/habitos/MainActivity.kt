package com.example.habitos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitos.ui.navigation.AppNavigation
import com.example.habitos.ui.theme.HabitosTheme
import com.example.habitos.viewmodel.AuthViewModel
import com.example.habitos.viewmodel.HabitViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitosTheme {
                val authViewModel: AuthViewModel = viewModel()
                val habitViewModel: HabitViewModel = viewModel()
                AppNavigation(
                    authViewModel = authViewModel,
                    habitViewModel = habitViewModel
                )
            }
        }
    }
}
