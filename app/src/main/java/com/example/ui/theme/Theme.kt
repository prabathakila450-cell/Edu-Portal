package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = BlueOnPrimaryDark,
    primaryContainer = BluePrimaryContainerDark,
    onPrimaryContainer = BlueOnPrimaryContainerDark,
    background = BackgroundDark,
    onBackground = BlueOnPrimaryContainerDark,
    surface = SurfaceDark,
    onSurface = BlueOnPrimaryContainerDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = NeutralMediumDark,
    outline = NeutralBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BluePrimary,
    onPrimary = BlueOnPrimary,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = BlueOnPrimaryContainer,
    background = BackgroundLight,
    onBackground = NeutralDark,
    surface = SurfaceCardLight,
    onSurface = NeutralDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = NeutralMedium,
    outline = NeutralBorder,
    outlineVariant = NeutralBorderLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  // Disable dynamic color by default to preserve custom theme aesthetic
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

