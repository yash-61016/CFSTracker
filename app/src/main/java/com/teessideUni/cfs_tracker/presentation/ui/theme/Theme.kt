package com.teessideUni.cfs_tracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


// Material 3 color schemes
private val cfsDarkColorScheme = darkColorScheme(
    primary = cfsDarkPrimary,
    onPrimary = cfsDarkOnPrimary,
    primaryContainer = cfsDarkPrimaryContainer,
    onPrimaryContainer = cfsDarkOnPrimaryContainer,
    inversePrimary = cfsDarkPrimaryInverse,
    secondary = cfsDarkSecondary,
    onSecondary = cfsDarkOnSecondary,
    secondaryContainer = cfsDarkSecondaryContainer,
    onSecondaryContainer = cfsDarkOnSecondaryContainer,
    tertiary = cfsDarkTertiary,
    onTertiary = cfsDarkOnTertiary,
    tertiaryContainer = cfsDarkTertiaryContainer,
    onTertiaryContainer = cfsDarkOnTertiaryContainer,
    error = cfsDarkError,
    onError = cfsDarkOnError,
    errorContainer = cfsDarkErrorContainer,
    onErrorContainer = cfsDarkOnErrorContainer,
    background = cfsDarkBackground,
    onBackground = cfsDarkOnBackground,
    surface = cfsDarkSurface,
    onSurface = cfsDarkOnSurface,
    inverseSurface = cfsDarkInverseSurface,
    inverseOnSurface = cfsDarkInverseOnSurface,
    surfaceVariant = cfsDarkSurfaceVariant,
    onSurfaceVariant = cfsDarkOnSurfaceVariant,
    outline = cfsDarkOutline
)

private val cfsLightColorScheme = lightColorScheme(
    primary = cfsLightPrimary,
    onPrimary = cfsLightOnPrimary,
    primaryContainer = cfsLightPrimaryContainer,
    onPrimaryContainer = cfsLightOnPrimaryContainer,
    inversePrimary = cfsLightPrimaryInverse,
    secondary = cfsLightSecondary,
    onSecondary = cfsLightOnSecondary,
    secondaryContainer = cfsLightSecondaryContainer,
    onSecondaryContainer = cfsLightOnSecondaryContainer,
    tertiary = cfsLightTertiary,
    onTertiary = cfsLightOnTertiary,
    tertiaryContainer = cfsLightTertiaryContainer,
    onTertiaryContainer = cfsLightOnTertiaryContainer,
    error = cfsLightError,
    onError = cfsLightOnError,
    errorContainer = cfsLightErrorContainer,
    onErrorContainer = cfsLightOnErrorContainer,
    background = cfsLightBackground,
    onBackground = cfsLightOnBackground,
    surface = cfsLightSurface,
    onSurface = cfsLightOnSurface,
    inverseSurface = cfsLightInverseSurface,
    inverseOnSurface = cfsLightInverseOnSurface,
    surfaceVariant = cfsLightSurfaceVariant,
    onSurfaceVariant = cfsLightOnSurfaceVariant,
    outline = cfsLightOutline
)
@Composable
fun CFSTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit) {

    val cfsColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
        darkTheme -> cfsDarkColorScheme
        else -> cfsLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = cfsColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = cfsColorScheme,
        typography = cfsTypography,
        shapes = shapes,
        content = content
    )
}