package com.shohankhan.ledgerly.domain.model

import com.shohankhan.ledgerly.domain.money.Minor

/**
 * Lightweight, persistence-free records that the domain layer works with.
 *
 * Repositories map Room entities onto these, so every calculation below can be
 * unit tested on a plain JVM without a database.
 */

data class RawShop(
    val id: Long,
    val name: String,
    val ownerName: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAt: Long = 0L
)

data class RawPurchase(
    val id: Long,
    val shopId: Long,
    val dateEpochDay: Long,
    val dueEpochDay: Long? = null,
    val totalMinor: Minor,
    val note: String? = null
)

data class RawPurchaseItem(
    val id: Long,
    val purchaseId: Long,
    val name: String,
    val quantityMilli: Long,
    val unitPriceMinor: Minor,
    val totalMinor: Minor
)

data class RawLoan(
    val id: Long,
    val institution: String,
    val title: String,
    val principalMinor: Minor,
    val totalPayableMinor: Minor,
    val installmentAmountMinor: Minor,
    val installmentCount: Int,
    val frequency: Frequency,
    val intervalCount: Int = 1,
    val customDays: Int = 0,
    val interestRatePct: Double? = null,
    val startDateEpochDay: Long,
    val firstDueEpochDay: Long,
    val finalDueEpochDay: Long? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAt: Long = 0L
)

data class RawEmi(
    val id: Long,
    val product: String,
    val seller: String? = null,
    val purchaseEpochDay: Long,
    val cashPriceMinor: Minor? = null,
    val financedMinor: Minor,
    val downPaymentMinor: Minor = 0L,
    val totalPayableMinor: Minor,
    val installmentAmountMinor: Minor,
    val installmentCount: Int,
    val frequency: Frequency,
    val intervalCount: Int = 1,
    val customDays: Int = 0,
    val firstDueEpochDay: Long,
    val finalDueEpochDay: Long? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAt: Long = 0L
)

/** Shared shape for loan and EMI instalments ([parentId] is the loan/EMI id). */
data class RawInstallment(
    val id: Long,
    val parentId: Long,
    val indexNo: Int,
    val dueEpochDay: Long,
    val amountMinor: Minor,
    val paidMinor: Minor = 0L,
    val status: String = "PENDING",
    val paidEpochDay: Long? = null
) {
    val outstandingMinor: Minor get() = (amountMinor - paidMinor).coerceAtLeast(0L)
    val isSettled: Boolean get() = paidMinor >= amountMinor
}

data class RawPerson(
    val id: Long,
    val name: String,
    val relationship: String = "Friend",
    val phone: String? = null,
    val email: String? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAt: Long = 0L
)

data class RawBorrowing(
    val id: Long,
    val personId: Long,
    val amountMinor: Minor,
    val borrowedEpochDay: Long,
    val dueEpochDay: Long? = null,
    val reason: String? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAt: Long = 0L
)

data class RawPayment(
    val id: Long,
    val debtType: DebtType,
    val debtId: Long,
    val amountMinor: Minor,
    val dateEpochDay: Long,
    val note: String? = null
)

data class RawIncome(
    val id: Long,
    val amountMinor: Minor,
    val dateEpochDay: Long,
    val source: String,
    val category: String,
    val description: String? = null,
    val note: String? = null
)

data class RawExpense(
    val id: Long,
    val amountMinor: Minor,
    val dateEpochDay: Long,
    val category: String,
    val merchant: String? = null,
    val description: String? = null,
    val note: String? = null
)

/** One line of the complete transaction ledger. */
data class LedgerEntry(
    val id: Long,
    val type: TransactionType,
    val amountMinor: Minor,
    val dateEpochDay: Long,
    val dateTimeMillis: Long,
    val title: String,
    val subtitle: String? = null,
    val category: String? = null,
    val relatedType: DebtType? = null,
    val relatedId: Long? = null,
    val note: String? = null
)

/** Everything the debt engine needs, in one immutable value. */
data class DebtSnapshot(
    val shops: List<RawShop> = emptyList(),
    val purchases: List<RawPurchase> = emptyList(),
    val loans: List<RawLoan> = emptyList(),
    val loanInstallments: List<RawInstallment> = emptyList(),
    val emis: List<RawEmi> = emptyList(),
    val emiInstallments: List<RawInstallment> = emptyList(),
    val people: List<RawPerson> = emptyList(),
    val borrowings: List<RawBorrowing> = emptyList(),
    val payments: List<RawPayment> = emptyList()
)
