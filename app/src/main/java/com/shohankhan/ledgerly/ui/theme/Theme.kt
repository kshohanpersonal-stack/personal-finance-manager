package com.shohankhan.ledgerly.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Ledgerly ships a single, carefully tuned light theme.
 *
 * The system day-night flag is intentionally ignored ([isSystemInDarkTheme] is
 * never consulted): the app stays light even when the device is in dark mode.
 */
private val LedgerlyLightColors = lightColorScheme(
    primary = LedgerlyColors.Primary,
    onPrimary = LedgerlyColors.Surface,
    primaryContainer = LedgerlyColors.PrimarySoft,
    onPrimaryContainer = LedgerlyColors.PrimaryStrong,
    secondary = LedgerlyColors.Teal,
    onSecondary = LedgerlyColors.Surface,
    secondaryContainer = LedgerlyColors.TealSoft,
    onSecondaryContainer = Color(0xFF0B5C55),
    tertiary = LedgerlyColors.Cyan,
    onTertiary = LedgerlyColors.Surface,
    tertiaryContainer = LedgerlyColors.CyanSoft,
    onTertiaryContainer = Color(0xFF0B5C6B),
    background = LedgerlyColors.Canvas,
    onBackground = LedgerlyColors.Ink,
    surface = LedgerlyColors.Surface,
    onSurface = LedgerlyColors.Ink,
    surfaceVariant = LedgerlyColors.SurfaceSunken,
    onSurfaceVariant = LedgerlyColors.Body,
    surfaceTint = LedgerlyColors.Primary,
    outline = LedgerlyColors.OutlineStrong,
    outlineVariant = LedgerlyColors.Outline,
    error = LedgerlyColors.Overdue,
    onError = LedgerlyColors.Surface,
    errorContainer = LedgerlyColors.OverdueSoft,
    onErrorContainer = Color(0xFF8C1D1D),
    scrim = Color(0x990B1729)
)

private val LedgerlyShapes = Shapes(
    extraSmall = RoundedCornerShape(Radius.chip),
    small = RoundedCornerShape(Radius.input),
    medium = RoundedCornerShape(Radius.card),
    large = RoundedCornerShape(Radius.cardLarge),
    extraLarge = RoundedCornerShape(Radius.sheet)
)

@Composable
fun LedgerlyTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = LedgerlyColors.Canvas.toArgb()
            window.navigationBarColor = LedgerlyColors.Surface.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }
    MaterialTheme(
        colorScheme = LedgerlyLightColors,
        typography = LedgerlyTypography,
        shapes = LedgerlyShapes,
        content = content
    )
}
