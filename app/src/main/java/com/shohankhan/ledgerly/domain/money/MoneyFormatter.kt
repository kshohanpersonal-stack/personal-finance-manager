package com.shohankhan.ledgerly.domain.money

import java.text.NumberFormat
import java.util.Locale

/**
 * Single source of truth for how money looks on screen.
 *
 * Rules:
 *  • symbol first, then the grouped amount;
 *  • trailing ".00" is dropped unless [forceDecimals] is set;
 *  • the minus sign always precedes the symbol;
 *  • grouping always uses the international style so figures stay readable.
 */
object MoneyFormatter {

    fun format(
        minor: Minor,
        currency: CurrencyDef,
        forceDecimals: Boolean = false,
        signed: Boolean = false
    ): String {
        val scale = currency.fractionDigits
        val negative = minor < 0
        val abs = kotlin.math.abs(minor)
        val formatter = NumberFormat.getInstance(Locale.US).apply {
            isGroupingUsed = true
            minimumFractionDigits = if (scale == 0) 0 else if (forceDecimals) scale else 0
            maximumFractionDigits = scale
        }
        val number = formatter.format(Money.minorToDecimal(abs, scale))
        val sign = when {
            negative -> "-"
            signed && minor > 0 -> "+"
            else -> ""
        }
        return "$sign${currency.symbol}$number"
    }

    /** Compact form for chart axes and dense tiles: ৳12.5K, ৳1.2M. */
    fun compact(minor: Minor, currency: CurrencyDef): String {
        val scale = currency.fractionDigits
        val negative = minor < 0
        val abs = kotlin.math.abs(minor).toDouble() / pow10(scale)
        val (value, suffix) = when {
            abs >= 1_000_000_000.0 -> abs / 1_000_000_000.0 to "B"
            abs >= 1_000_000.0 -> abs / 1_000_000.0 to "M"
            abs >= 1_000.0 -> abs / 1_000.0 to "K"
            else -> abs to ""
        }
        val text = if (value >= 100 || suffix.isEmpty()) {
            value.toBigDecimal().setScale(if (suffix.isEmpty()) 0 else 0, java.math.RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString()
        } else {
            String.format(Locale.US, "%.1f", value)
        }
        val sign = if (negative) "-" else ""
        return "$sign${currency.symbol}$text$suffix"
    }

    /** Plain decimal string (no symbol, no grouping) used for editors and CSV/JSON. */
    fun plain(minor: Minor, currency: CurrencyDef): String =
        Money.minorToDecimal(minor, currency.fractionDigits).toPlainString()

    private fun pow10(scale: Int): Double {
        var result = 1.0
        repeat(scale) { result *= 10.0 }
        return result
    }
}
