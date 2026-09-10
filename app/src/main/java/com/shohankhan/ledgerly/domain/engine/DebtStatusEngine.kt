package com.shohankhan.ledgerly.domain.engine

import com.shohankhan.ledgerly.domain.model.DebtStatus
import com.shohankhan.ledgerly.domain.money.Minor

/**
 * The single place where the status of a debt is decided.
 *
 *  0 paid and nothing outstanding yet → UNPAID
 *  some paid, something still owed    → PARTIAL
 *  nothing left to pay                → PAID
 *  past the due date and still owed   → OVERDUE
 */
object DebtStatusEngine {

    /** Days before the due date at which a debt starts counting as "due soon". */
    const val DUE_SOON_DAYS = 7L

    fun statusOf(
        paidMinor: Minor,
        remainingMinor: Minor,
        dueEpochDay: Long?,
        todayEpochDay: Long
    ): DebtStatus {
        if (remainingMinor <= 0L) return DebtStatus.PAID
        if (dueEpochDay != null && dueEpochDay < todayEpochDay && remainingMinor > 0L) {
            return DebtStatus.OVERDUE
        }
        return if (paidMinor > 0L) DebtStatus.PARTIAL else DebtStatus.UNPAID
    }

    fun isDueSoon(
        dueEpochDay: Long?,
        todayEpochDay: Long,
        remainingMinor: Minor
    ): Boolean {
        if (remainingMinor <= 0L || dueEpochDay == null) return false
        return dueEpochDay in todayEpochDay..(todayEpochDay + DUE_SOON_DAYS)
    }

    fun matches(
        status: DebtStatus,
        paidMinor: Minor,
        remainingMinor: Minor,
        dueEpochDay: Long?,
        todayEpochDay: Long,
        filter: com.shohankhan.ledgerly.domain.model.DebtFilter
    ): Boolean = when (filter) {
        com.shohankhan.ledgerly.domain.model.DebtFilter.ALL -> true
        com.shohankhan.ledgerly.domain.model.DebtFilter.UNPAID -> status == DebtStatus.UNPAID
        com.shohankhan.ledgerly.domain.model.DebtFilter.PARTIAL -> status == DebtStatus.PARTIAL
        com.shohankhan.ledgerly.domain.model.DebtFilter.PAID -> status == DebtStatus.PAID
        com.shohankhan.ledgerly.domain.model.DebtFilter.OVERDUE -> status == DebtStatus.OVERDUE
        com.shohankhan.ledgerly.domain.model.DebtFilter.DUE_SOON ->
            isDueSoon(dueEpochDay, todayEpochDay, remainingMinor)
    }
}
