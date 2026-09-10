package com.shohankhan.ledgerly.domain.engine

import com.shohankhan.ledgerly.domain.model.DebtSnapshot
import com.shohankhan.ledgerly.domain.model.DebtStatus
import com.shohankhan.ledgerly.domain.model.DebtSummary
import com.shohankhan.ledgerly.domain.model.DebtType
import com.shohankhan.ledgerly.domain.model.RawInstallment
import com.shohankhan.ledgerly.domain.money.Minor
import com.shohankhan.ledgerly.domain.money.Money

/**
 * Turns raw persisted records into the unified [DebtSummary] list used by every
 * screen. Pure function — no database, no Android APIs — so the money rules in
 * the product spec are covered by plain JVM unit tests.
 */
object DebtSummaryBuilder {

    fun build(snapshot: DebtSnapshot, todayEpochDay: Long): List<DebtSummary> {
        val paidByDebt: Map<Pair<DebtType, Long>, Minor> = snapshot.payments
            .groupBy { it.debtType to it.debtId }
            .mapValues { (_, payments) -> Money.sum(payments.map { it.amountMinor }) }

        fun paidFor(type: DebtType, id: Long): Minor = paidByDebt[type to id] ?: 0L

        val shopSummaries = snapshot.shops.map { shop ->
            val shopPurchases = snapshot.purchases.filter { it.shopId == shop.id }
            val original = Money.sum(shopPurchases.map { it.totalMinor })
            val paid = paidFor(DebtType.SHOP, shop.id)
            val remaining = Money.subtract(original, paid)
            val due = if (remaining > 0L) {
                shopPurchases.mapNotNull { it.dueEpochDay }.minOrNull()
            } else {
                null
            }
            val count = shopPurchases.size
            DebtSummary(
                type = DebtType.SHOP,
                id = shop.id,
                title = shop.name,
                creditor = shop.name,
                subtitle = "$count ${if (count == 1) "purchase" else "purchases"}",
                originalMinor = original,
                paidMinor = paid,
                remainingMinor = remaining,
                dueEpochDay = due,
                status = DebtStatusEngine.statusOf(paid, remaining, due, todayEpochDay),
                archived = shop.archived,
                createdAtMillis = shop.createdAt,
                note = shop.note
            )
        }

        val loanSummaries = snapshot.loans.map { loan ->
            val installments = snapshot.loanInstallments.filter { it.parentId == loan.id }
            val paid = paidFor(DebtType.LOAN, loan.id)
            val remaining = Money.subtract(loan.totalPayableMinor, paid)
            val due = nextDueEpochDay(installments, remaining, loan.finalDueEpochDay)
            val paidCount = installments.count { it.isSettled }
            DebtSummary(
                type = DebtType.LOAN,
                id = loan.id,
                title = loan.title,
                creditor = loan.institution,
                subtitle = if (installments.isNotEmpty()) {
                    "$paidCount / ${installments.size} instalments"
                } else {
                    InstallmentEngine.label(loan.frequency, loan.intervalCount, loan.customDays)
                },
                originalMinor = loan.totalPayableMinor,
                paidMinor = paid,
                remainingMinor = remaining,
                dueEpochDay = due,
                status = DebtStatusEngine.statusOf(paid, remaining, due, todayEpochDay),
                paidInstallments = paidCount,
                totalInstallments = installments.size,
                archived = loan.archived,
                createdAtMillis = loan.createdAt,
                note = loan.note
            )
        }

        val emiSummaries = snapshot.emis.map { emi ->
            val installments = snapshot.emiInstallments.filter { it.parentId == emi.id }
            val paid = paidFor(DebtType.EMI, emi.id)
            val remaining = Money.subtract(emi.totalPayableMinor, paid)
            val due = nextDueEpochDay(installments, remaining, emi.finalDueEpochDay)
            val paidCount = installments.count { it.isSettled }
            DebtSummary(
                type = DebtType.EMI,
                id = emi.id,
                title = emi.product,
                creditor = emi.seller ?: "EMI purchase",
                subtitle = if (installments.isNotEmpty()) {
                    "$paidCount / ${installments.size} EMIs"
                } else {
                    InstallmentEngine.label(emi.frequency, emi.intervalCount, emi.customDays)
                },
                originalMinor = emi.totalPayableMinor,
                paidMinor = paid,
                remainingMinor = remaining,
                dueEpochDay = due,
                status = DebtStatusEngine.statusOf(paid, remaining, due, todayEpochDay),
                paidInstallments = paidCount,
                totalInstallments = installments.size,
                archived = emi.archived,
                createdAtMillis = emi.createdAt,
                note = emi.note
            )
        }

        val peopleById = snapshot.people.associateBy { it.id }
        val borrowingSummaries = snapshot.borrowings.map { borrowing ->
            val person = peopleById[borrowing.personId]
            val paid = paidFor(DebtType.PERSON, borrowing.id)
            val remaining = Money.subtract(borrowing.amountMinor, paid)
            val due = if (remaining > 0L) borrowing.dueEpochDay else null
            DebtSummary(
                type = DebtType.PERSON,
                id = borrowing.id,
                title = person?.name ?: "Person",
                creditor = person?.name ?: "Person",
                subtitle = borrowing.reason
                    ?: person?.relationship
                    ?: "Borrowed",
                originalMinor = borrowing.amountMinor,
                paidMinor = paid,
                remainingMinor = remaining,
                dueEpochDay = due,
                status = DebtStatusEngine.statusOf(paid, remaining, due, todayEpochDay),
                archived = borrowing.archived || person?.archived == true,
                createdAtMillis = borrowing.createdAt,
                note = borrowing.note
            )
        }

        return (shopSummaries + loanSummaries + emiSummaries + borrowingSummaries)
            .sortedWith(compareBy({ it.archived }, { it.remainingMinor == 0L }, { it.dueEpochDay ?: Long.MAX_VALUE }))
    }

    private fun nextDueEpochDay(
        installments: List<RawInstallment>,
        remaining: Minor,
        fallbackDue: Long?
    ): Long? {
        if (remaining <= 0L) return null
        val next = installments
            .filter { !it.isSettled }
            .minByOrNull { it.indexNo }
            ?.dueEpochDay
        return next ?: fallbackDue
    }
}

/** Portfolio level figures derived from the unified summaries. */
data class DebtTotals(
    val originalMinor: Minor = 0L,
    val paidMinor: Minor = 0L,
    val outstandingMinor: Minor = 0L,
    val overdueMinor: Minor = 0L,
    val dueSoonMinor: Minor = 0L,
    val byType: Map<DebtType, Minor> = emptyMap()
) {
    val repaymentRate: Float get() = Money.ratio(paidMinor, originalMinor)
}

object DebtTotalsCalculator {

    fun compute(summaries: List<DebtSummary>, todayEpochDay: Long): DebtTotals {
        val active = summaries.filter { !it.archived }
        val byType = DebtType.values().associateWith { type ->
            Money.sum(active.filter { it.type == type }.map { it.remainingMinor })
        }
        return DebtTotals(
            originalMinor = Money.sum(active.map { it.originalMinor }),
            paidMinor = Money.sum(active.map { it.paidMinor }),
            outstandingMinor = Money.sum(active.map { it.remainingMinor }),
            overdueMinor = Money.sum(
                active.filter { it.status == DebtStatus.OVERDUE }.map { it.remainingMinor }
            ),
            dueSoonMinor = Money.sum(
                active.filter {
                    DebtStatusEngine.isDueSoon(it.dueEpochDay, todayEpochDay, it.remainingMinor)
                }.map { it.remainingMinor }
            ),
            byType = byType
        )
    }
}
