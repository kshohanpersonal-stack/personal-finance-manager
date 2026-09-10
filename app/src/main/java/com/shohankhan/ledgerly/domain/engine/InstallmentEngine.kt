package com.shohankhan.ledgerly.domain.engine

import com.shohankhan.ledgerly.domain.model.Frequency
import com.shohankhan.ledgerly.domain.money.Minor
import com.shohankhan.ledgerly.domain.money.Money
import java.time.LocalDate
import kotlin.math.min

/**
 * Builds repayment schedules that are always valid calendar dates.
 *
 * Monthly and quarterly schedules are anchored on the day of month of the first
 * due date and clamped to the length of the target month, so 31 Jan rolls to
 * 28 Feb (29 Feb in a leap year) instead of overflowing into March.
 */
object InstallmentEngine {

    fun generate(
        firstDueEpochDay: Long,
        count: Int,
        frequency: Frequency,
        interval: Int = 1,
        customDays: Int = 0
    ): List<Long> {
        if (count <= 0) return emptyList()
        val start = LocalDate.ofEpochDay(firstDueEpochDay)
        val step = interval.coerceAtLeast(1)
        return List(count) { index -> dateAt(start, index, frequency, step, customDays).toEpochDay() }
    }

    fun finalDue(
        firstDueEpochDay: Long,
        count: Int,
        frequency: Frequency,
        interval: Int = 1,
        customDays: Int = 0
    ): Long? = generate(firstDueEpochDay, count, frequency, interval, customDays).lastOrNull()

    private fun dateAt(
        start: LocalDate,
        index: Int,
        frequency: Frequency,
        step: Int,
        customDays: Int
    ): LocalDate = when (frequency) {
        Frequency.WEEKLY -> start.plusDays(7L * step * index)
        Frequency.BIWEEKLY -> start.plusDays(14L * step * index)
        Frequency.MONTHLY -> monthAt(start, step * index)
        Frequency.QUARTERLY -> monthAt(start, step * 3 * index)
        Frequency.CUSTOM -> {
            val days = if (customDays > 0) customDays else 30
            start.plusDays(days.toLong() * step * index)
        }
    }

    /** Adds months while clamping the day-of-month to the target month length. */
    fun monthAt(anchor: LocalDate, monthsToAdd: Int): LocalDate {
        if (monthsToAdd == 0) return anchor
        val target = anchor.plusMonths(monthsToAdd.toLong())
        val day = min(anchor.dayOfMonth, target.lengthOfMonth())
        return target.withDayOfMonth(day)
    }

    /**
     * Splits [totalMinor] into [count] instalments without losing a single minor
     * unit: the remainder is spread over the earliest instalments.
     */
    fun splitAmounts(totalMinor: Minor, count: Int): List<Minor> {
        if (count <= 0 || totalMinor <= 0L) return emptyList()
        val base = totalMinor / count
        var remainder = totalMinor - base * count
        return List(count) { index ->
            val extra = if (remainder > 0) {
                remainder--
                1L
            } else {
                0L
            }
            base + extra
        }
    }

    /** Equal instalment rounded up to the nearest minor unit. */
    fun suggestedInstallmentAmount(totalMinor: Minor, count: Int): Minor {
        if (count <= 0 || totalMinor <= 0L) return 0L
        val base = totalMinor / count
        return if (totalMinor % count == 0L) base else base + 1L
    }

    /** Total payable when only a principal and a flat interest rate are known. */
    fun totalPayable(principalMinor: Minor, interestRatePct: Double?, frequency: Frequency?): Minor {
        if (interestRatePct == null || interestRatePct <= 0.0) return principalMinor
        val factor = 1.0 + interestRatePct / 100.0
        return Money.decimalToMinor(
            Money.minorToDecimal(principalMinor, 2).multiply(java.math.BigDecimal.valueOf(factor)),
            2
        )
    }

    fun label(frequency: Frequency, interval: Int, customDays: Int): String = when (frequency) {
        Frequency.WEEKLY -> if (interval == 1) "Weekly" else "Every $interval weeks"
        Frequency.BIWEEKLY -> if (interval == 1) "Biweekly" else "Every ${interval * 2} weeks"
        Frequency.MONTHLY -> if (interval == 1) "Monthly" else "Every $interval months"
        Frequency.QUARTERLY -> if (interval == 1) "Quarterly" else "Every ${interval * 3} months"
        Frequency.CUSTOM -> "Every ${if (customDays > 0) customDays else 30} days"
    }
}
