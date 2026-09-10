package com.shohankhan.ledgerly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LedgerlyFont = FontFamily.Default

/** Tabular numerals keep financial columns perfectly aligned. */
private fun numeric(style: TextStyle): TextStyle = style.copy(fontFeatureSettings = "tnum")

val LedgerlyTypography = Typography(
    displayLarge = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 40.sp,
            letterSpacing = (-0.6).sp
        )
    ),
    displayMedium = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.4).sp
        )
    ),
    headlineSmall = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = (-0.2).sp
        )
    ),
    headlineMedium = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            letterSpacing = (-0.3).sp
        )
    ),
    titleLarge = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp
        )
    ),
    titleMedium = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )
    ),
    titleSmall = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    ),
    bodyLarge = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            lineHeight = 22.sp
        )
    ),
    bodyMedium = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    ),
    bodySmall = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    ),
    labelLarge = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    ),
    labelMedium = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.Medium,
            fontSize = 12.5.sp,
            lineHeight = 16.sp
        )
    ),
    labelSmall = numeric(
        TextStyle(
            fontFamily = LedgerlyFont,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp
        )
    )
)
