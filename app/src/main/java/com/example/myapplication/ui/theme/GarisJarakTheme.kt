package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class GarisJarakColors(
    val background: Color = WarmIvoryBg,
    val surface: Color = WarmIvorySurface,
    val surfaceAlt: Color = WarmIvorySurfaceAlt,
    val border: Color = WarmIvoryBorder,
    val textPrimary: Color = CharcoalInkText,
    val textSecondary: Color = CharcoalInkSubtext,
    val textMuted: Color = CharcoalInkMuted,
    val accentBrick: Color = BrickRedPrimary,
    val accentSage: Color = MutedSageAccent,
    val sageLight: Color = MutedSageLight,
    val axisLine: Color = AxisLineColor,
    val gridLine: Color = GridLineColor
)

val LocalGarisJarakColors = compositionLocalOf { GarisJarakColors() }

object GarisJarakThemeTokens {
    val colors: GarisJarakColors
        @Composable
        @ReadOnlyComposable
        get() = LocalGarisJarakColors.current
}

@Composable
fun GarisJarakTheme(
    content: @Composable () -> Unit
) {
    val colors = GarisJarakColors()

    val materialScheme = lightColorScheme(
        primary = colors.accentBrick,
        secondary = colors.accentSage,
        background = colors.background,
        surface = colors.surface,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = colors.textPrimary,
        onSurface = colors.textPrimary
    )

    CompositionLocalProvider(LocalGarisJarakColors provides colors) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = GarisJarakTypography,
            content = content
        )
    }
}
