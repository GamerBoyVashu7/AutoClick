package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    secondary = DiamondCyan,
    onSecondary = DiamondOnCyan,
    secondaryContainer = DiamondCyanContainer,
    onSecondaryContainer = DiamondOnCyanContainer,
    tertiary = GoldAccent,
    background = ObsidianDark,
    onBackground = TextPrimary,
    surface = BedrockSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = SlateOutline,
    outlineVariant = SlateOutlineVariant,
    error = RedstoneError,
    onError = RedstoneOnError,
    errorContainer = RedstoneErrorContainer,
    onErrorContainer = RedstoneOnErrorContainer
)

private val LightColorScheme = DarkColorScheme // Always maintain premium gaming dark theme for contrast & Minecraft OTG focus

@Composable
fun GamerBoyVashuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
