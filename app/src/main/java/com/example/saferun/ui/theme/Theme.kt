package com.example.saferun.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SafeRunDayColors = lightColorScheme(
    primary = RunGreenDark,
    onPrimary = Color.White,
    primaryContainer = RunGreenSoft,
    onPrimaryContainer = Color(0xFF00391A),
    secondary = Color(0xFF00695C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8F6EC),
    onSecondaryContainer = Color(0xFF00251F),
    tertiary = Color(0xFF8A5100),
    onTertiary = Color.White,
    tertiaryContainer = SignalAmberSoft,
    onTertiaryContainer = Color(0xFF2A1800),
    background = DayBackground,
    onBackground = DayText,
    surface = DaySurface,
    onSurface = DayText,
    surfaceVariant = DaySurfaceMuted,
    onSurfaceVariant = DayTextMuted,
    outline = DayOutline,
    outlineVariant = Color(0xFFDCE5F0),
    error = SignalRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val SafeRunNightColors = darkColorScheme(
    primary = RunGreenBright,
    onPrimary = Color(0xFF00210C),
    primaryContainer = Color(0xFF00522A),
    onPrimaryContainer = Color(0xFFB9F6CA),
    secondary = Color(0xFF7DE0C8),
    onSecondary = Color(0xFF00382D),
    secondaryContainer = Color(0xFF005142),
    onSecondaryContainer = Color(0xFF9CF2DD),
    tertiary = SignalAmber,
    onTertiary = Color(0xFF2A1800),
    tertiaryContainer = Color(0xFF5C3D00),
    onTertiaryContainer = SignalAmberSoft,
    background = NightBackground,
    onBackground = NightText,
    surface = NightSurface,
    onSurface = NightText,
    surfaceVariant = NightSurfaceMuted,
    onSurfaceVariant = NightTextMuted,
    outline = NightOutline,
    outlineVariant = Color(0xFF1B2733),
    error = SignalRedBright,
    onError = Color(0xFF2A0000),
    errorContainer = Color(0xFF5C0000),
    onErrorContainer = Color(0xFFFFDAD6),
)

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * SafeRun theme. [night] switches to the high contrast night palette used by the
 * automatic lighting feature (HU-03).
 */
@Composable
fun SafeRunTheme(
    night: Boolean = false,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !night
                isAppearanceLightNavigationBars = !night
            }
        }
    }

    MaterialTheme(
        colorScheme = if (night) SafeRunNightColors else SafeRunDayColors,
        typography = SafeRunTypography,
        content = content,
    )
}
