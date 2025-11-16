package com.example.levelup_gamerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.levelup_gamerapp.ui.AppNavHost  // ✅ IMPORTA AppNavHost
import com.example.levelup_gamerapp.ui.theme.LevelUpGamerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LevelUpGamerAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost() // ✅ Cambia aquí
                }
            }
        }
    }
}
