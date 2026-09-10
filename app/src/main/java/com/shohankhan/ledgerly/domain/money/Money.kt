package com.shohankhan.ledgerly.domain.money

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Every monetary amount in Ledgerly is a [Long] count of minor units
 * (taka paisa, dollar cents, …). No monetary value is ever stored or
 * accumulated in a floating point type, so totals are exact and deterministic.
 */
typealias Minor = Long

object Money {

    const val ZERO: Minor = 0L

    /** Saturating addition — never overflows into a wrong figure. */
    fun add(a: Minor, b: Minor): Minor {
        val sum = a + b
        val overflow = (a xor sum) and (b xor sum) < 0
        return if (overflow) Long.MAX_VALUE else sum
    }

    fun sum(values: Iterable<Minor>): Minor = values.fold(ZERO) { acc, v -> add(acc, v) }

    /** Money owed can never go below zero. */
    fun subtract(a: Minor, b: Minor): Minor = (a - b).coerceAtLeast(0)

    fun clampAtLeastZero(value: Minor): Minor = value.coerceAtLeast(0)

    fun multiply(value: Minor, factor: Double, scale: Int): Minor =
        decimalToMinor(minorToDecimal(value, scale).multiply(BigDecimal.valueOf(factor)), scale)

    /** quantity × unit price, rounded once, half-up — the only rounding rule in the app. */
    fun lineTotal(quantityMilli: Long, unitPriceMinor: Minor, scale: Int): Minor {
        val quantity = BigDecimal.valueOf(quantityMilli, 3)
        val unit = minorToDecimal(unitPriceMinor, scale)
        return decimalToMinor(quantity.multiply(unit), scale)
    }

    fun minorToDecimal(value: Minor, scale: Int): BigDecimal =
        BigDecimal.valueOf(value, scale)

    fun decimalToMinor(value: BigDecimal, scale: Int): Minor =
        value.setScale(scale, RoundingMode.HALF_UP)
            .movePointRight(scale)
            .setScale(0, RoundingMode.HALF_UP)
            .toLong()

    fun majorToMinor(major: Double, scale: Int): Minor =
        decimalToMinor(BigDecimal.valueOf(major), scale)

    /** 0 when [total] is zero, otherwise the share of [part] in [total] within 0f..1f. */
    fun ratio(part: Minor, total: Minor): Float =
        if (total <= 0) 0f else (part.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    fun percentage(part: Minor, total: Minor): Int =
        (ratio(part, total) * 100f).toInt().coerceIn(0, 100)

    /** Applies an annual percentage rate over [days] using simple interest. */
    fun simpleInterest(principal: Minor, annualRatePct: Double, days: Int, scale: Int): Minor {
        if (annualRatePct <= 0.0 || days <= 0) return ZERO
        val rate = BigDecimal.valueOf(annualRatePct)
            .multiply(BigDecimal.valueOf(days.toLong()))
            .divide(BigDecimal.valueOf(36500L), 10, RoundingMode.HALF_UP)
        return decimalToMinor(minorToDecimal(principal, scale).multiply(rate), scale)
    }
}

/**
 * Parses user typed amounts. Returns `null` for anything that is not a clean,
 * positive money value so callers can show a friendly validation message.
 */
object AmountParser {

    fun parse(input: String, currency: CurrencyDef): Minor? {
        val cleaned = normalise(input)
        if (cleaned.isEmpty()) return null
        return runCatching {
            val value = BigDecimal(cleaned)
            if (value <= BigDecimal.ZERO) return null
            Money.decimalToMinor(value, currency.fractionDigits)
        }.getOrNull()
    }

    /** Accepts "1,250.50", "1250.5", "৳ 1 250" and stray currency symbols. */
    fun normalise(input: String): String {
        var text = input.trim()
            .replace(" ", "")
            .replace(" ", "")
            .replace(Regex("[^0-9.,\\-]"), "")
        val lastDot = text.lastIndexOf('.')
        val lastComma = text.lastIndexOf(',')
        if (lastDot >= 0 && lastComma >= 0) {
            text = if (lastComma > lastDot) {
                // 1.234,56 (European) → 1234.56
                text.replace(".", "").replace(',', '.')
            } else {
                text.replace(",", "")
            }
        } else if (lastComma >= 0) {
            val decimals = text.length - lastComma - 1
            text = if (decimals == 3 && text.indexOf(',') == lastComma) {
                text.replace(",", "")
            } else {
                text.replace(',', '.')
            }
        }
        return text
    }

    /** Text shown while the user types: digits, at most one separator, at most `scale` decimals. */
    fun formatInput(input: String, currency: CurrencyDef): String {
        val scale = currency.fractionDigits
        if (scale == 0) return input.filter { it.isDigit() }
        var out = input
        val separatorIndex = maxOf(out.indexOf('.'), out.indexOf(','))
        if (separatorIndex >= 0) {
            val intPart = out.substring(0, separatorIndex).filter { it.isDigit() }
            val fracPart = out.substring(separatorIndex + 1).filter { it.isDigit() }.take(scale)
            out = if (separatorIndex == out.length - 1 && fracPart.isEmpty()) {
                "$intPart."
            } else {
                "$intPart.$fracPart"
            }
        } else {
            out = out.filter { it.isDigit() }
        }
        return out
    }
}
