package com.shohankhan.ledgerly.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Ledgerly light palette.
 *
 * The application is deliberately light-mode only: every colour below is tuned
 * for a bright surface and there is intentionally no dark counterpart.
 */
object LedgerlyColors {

    // Neutrals -------------------------------------------------------------
    val Ink = Color(0xFF0B1729)
    val InkSoft = Color(0xFF2A3A52)
    val Body = Color(0xFF47576E)
    val Muted = Color(0xFF6F7F94)
    val Faint = Color(0xFF9AA7B8)

    val Canvas = Color(0xFFF5F7FA)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceAlt = Color(0xFFF8FAFC)
    val SurfaceSunken = Color(0xFFEFF3F8)
    val Outline = Color(0xFFE6EBF2)
    val OutlineStrong = Color(0xFFD3DCE7)

    // Brand ----------------------------------------------------------------
    val Primary = Color(0xFF0E9F6E)
    val PrimaryStrong = Color(0xFF0A7C56)
    val PrimarySoft = Color(0xFFE4F6EE)
    val PrimaryTint = Color(0xFFF1FBF6)
    val Mint = Color(0xFF34D399)
    val Teal = Color(0xFF0D9488)
    val TealSoft = Color(0xFFE0F5F4)
    val Cyan = Color(0xFF06B6D4)
    val CyanSoft = Color(0xFFE0F7FB)

    // Semantic -------------------------------------------------------------
    val Income = Color(0xFF0E9F6E)
    val Expense = Color(0xFFE5484D)
    val ExpenseSoft = Color(0xFFFDECEC)
    val Overdue = Color(0xFFDC2626)
    val OverdueSoft = Color(0xFFFDEAEA)
    val Warning = Color(0xFFD97706)
    val WarningSoft = Color(0xFFFEF3E2)
    val Info = Color(0xFF2563EB)
    val InfoSoft = Color(0xFFE8F0FE)

    // Debt category accents -------------------------------------------------
    val Shop = Color(0xFF0EA5A5)
    val ShopSoft = Color(0xFFE1F6F6)
    val Loan = Color(0xFF6366F1)
    val LoanSoft = Color(0xFFEAECFE)
    val Emi = Color(0xFFD97706)
    val EmiSoft = Color(0xFFFEF3E2)
    val People = Color(0xFFDB2777)
    val PeopleSoft = Color(0xFFFCE8F2)

    // Gradients -------------------------------------------------------------
    val BrandGradient = listOf(Color(0xFF0FA968), Color(0xFF0D8F86))
    val HeroGradient = listOf(Color(0xFF0B1729), Color(0xFF123A34))
    val MintGradient = listOf(Color(0xFF34D399), Color(0xFF0EA5A5))
    val InkGradient = listOf(Color(0xFF16273D), Color(0xFF0B1729))
}
