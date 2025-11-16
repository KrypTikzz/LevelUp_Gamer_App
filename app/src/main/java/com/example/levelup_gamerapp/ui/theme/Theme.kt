package com.example.levelup_gamerapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AzulElectrico,
    secondary = VerdeNeon,
    background = NegroFondo,
    surface = NegroFondo,
    onPrimary = BlancoTexto,
    onSecondary = BlancoTexto,
    onBackground = BlancoTexto,
    onSurface = GrisClaroTexto
)

// 🧭 Tema principal
@Composable
fun LevelUpGamerAppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}