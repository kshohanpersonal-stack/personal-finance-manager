package com.shohankhan.ledgerly.domain.model

import com.shohankhan.ledgerly.domain.money.Minor
import com.shohankhan.ledgerly.domain.money.Money

enum class DebtType(val label: String, val shortLabel: String) {
    SHOP("Shop credit", "Shop"),
    LOAN("Loan", "Loan"),
    EMI("EMI", "EMI"),
    PERSON("Personal borrowing", "People")
}

enum class DebtStatus(val label: String) {
    UNPAID("Unpaid"),
    PARTIAL("Partially paid"),
    PAID("Paid"),
    OVERDUE("Overdue")
}

enum class Frequency(val label: String, val approxDays: Int) {
    WEEKLY("Weekly", 7),
    BIWEEKLY("Biweekly", 14),
    MONTHLY("Monthly", 30),
    QUARTERLY("Quarterly", 90),
    CUSTOM("Custom", 0)
}

enum class TransactionType(val label: String) {
    SHOP_PURCHASE("Credit purchase"),
    SHOP_PAYMENT("Shop payment"),
    LOAN_TAKEN("Loan taken"),
    LOAN_PAYMENT("Loan payment"),
    EMI_STARTED("EMI started"),
    EMI_PAYMENT("EMI payment"),
    BORROWED("Borrowed"),
    PERSONAL_REPAYMENT("Repayment"),
    INCOME("Income"),
    EXPENSE("Expense")
}

enum class CategoryKind { INCOME, EXPENSE }

enum class DebtFilter(val label: String) {
    ALL("All"),
    UNPAID("Unpaid"),
    PARTIAL("Partially paid"),
    PAID("Paid"),
    DUE_SOON("Due soon"),
    OVERDUE("Overdue")
}

enum class SortOption(val label: String) {
    DUE_DATE("Due date"),
    AMOUNT("Amount"),
    NAME("Name"),
    CREATED("Created"),
    STATUS("Status")
}

enum class TimeRange(val label: String) {
    WEEK("This week"),
    MONTH("This month"),
    YEAR("This year"),
    LAST_6_MONTHS("6 months"),
    CUSTOM("Custom")
}

/** One unified view over every kind of money the user owes. */
data class DebtSummary(
    val type: DebtType,
    val id: Long,
    val title: String,
    val creditor: String,
    val subtitle: String,
    val originalMinor: Minor,
    val paidMinor: Minor,
    val remainingMinor: Minor,
    val dueEpochDay: Long?,
    val status: DebtStatus,
    val paidInstallments: Int = 0,
    val totalInstallments: Int = 0,
    val archived: Boolean = false,
    val createdAtMillis: Long = 0L,
    val note: String? = null
) {
    val progress: Float get() = Money.ratio(paidMinor, originalMinor)
    val progressPercent: Int get() = Money.percentage(paidMinor, originalMinor)
    val isSettled: Boolean get() = remainingMinor <= 0L
    val hasSchedule: Boolean get() = totalInstallments > 0

    /** Stable key used by search, navigation and payment linking. */
    val key: String get() = "${type.name}-$id"
}

/** A dated obligation shown on the dashboard, schedule screen and in reminders. */
data class PaymentObligation(
    val debtType: DebtType,
    val debtId: Long,
    val title: String,
    val creditor: String,
    val amountMinor: Minor,
    val dueEpochDay: Long,
    val installmentLabel: String? = null,
    val paid: Boolean = false
) {
    val key: String get() = "${debtType.name}-$debtId-$dueEpochDay"
}

enum class ObligationGroup(val label: String) {
    OVERDUE("Overdue"),
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    THIS_WEEK("This week"),
    THIS_MONTH("This month"),
    LATER("Later")
}

data class CategoryTotal(
    val category: String,
    val amountMinor: Minor,
    val share: Float = 0f
)

data class MonthlyFlow(
    val year: Int,
    val month: Int,
    val incomeMinor: Minor = 0L,
    val expenseMinor: Minor = 0L,
    val debtPaymentMinor: Minor = 0L,
    val newDebtMinor: Minor = 0L
) {
    val netMinor: Minor get() = incomeMinor - expenseMinor
    val label: String get() = "${monthLabel(month)} ${year % 100}"
}

private fun monthLabel(month: Int): String = when (month) {
    1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"; 5 -> "May"; 6 -> "Jun"
    7 -> "Jul"; 8 -> "Aug"; 9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; else -> "Dec"
}
