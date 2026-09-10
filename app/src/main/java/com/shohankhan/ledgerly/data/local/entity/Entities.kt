package com.shohankhan.ledgerly.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local-first schema. Every monetary column is stored as integer minor units
 * (`*Minor`) and every date as an epoch day / epoch millis `Long`, which keeps
 * sorting, indexing and arithmetic exact and timezone-safe.
 */

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Long = 0L,
    val name: String? = null,
    val currencyCode: String = "BDT",
    val onboardingCompleted: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val reminderDaysBefore: Int = 1,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val appLockEnabled: Boolean = false,
    val appLockPinHash: String? = null,
    val biometricEnabled: Boolean = false,
    val dateFormat: String = "d MMM yyyy",
    val weekStartsOnMonday: Boolean = true,
    val createdAtMillis: Long = 0L
)

// ---------------------------------------------------------------- Shop credit

@Entity(tableName = "shops", indices = [Index("name"), Index("archived")])
data class ShopEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val ownerName: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAtMillis: Long = 0L
)

@Entity(
    tableName = "shop_purchases",
    indices = [Index("shopId"), Index("dateEpochDay"), Index("dueEpochDay")],
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shopId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShopPurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val shopId: Long,
    val dateEpochDay: Long,
    val dueEpochDay: Long? = null,
    /** Authoritative purchase total: the sum of its item totals. */
    val totalMinor: Long,
    val note: String? = null,
    val createdAtMillis: Long = 0L
)

@Entity(
    tableName = "shop_purchase_items",
    indices = [Index("purchaseId")],
    foreignKeys = [
        ForeignKey(
            entity = ShopPurchaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["purchaseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShopPurchaseItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val purchaseId: Long,
    val name: String,
    /** Quantity × 1000 so fractional quantities (1.5 kg) stay exact. */
    val quantityMilli: Long,
    val unitPriceMinor: Long,
    val totalMinor: Long
)

// ---------------------------------------------------------------------- Loans

@Entity(tableName = "loans", indices = [Index("firstDueEpochDay"), Index("archived")])
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val institution: String,
    val title: String,
    val principalMinor: Long,
    val interestRatePct: Double? = null,
    val totalPayableMinor: Long,
    val installmentAmountMinor: Long,
    val frequency: String,
    val intervalCount: Int = 1,
    val customDays: Int = 0,
    val installmentCount: Int,
    val startDateEpochDay: Long,
    val firstDueEpochDay: Long,
    val finalDueEpochDay: Long? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAtMillis: Long = 0L
)

const val INSTALLMENT_PENDING = "PENDING"
const val INSTALLMENT_PARTIAL = "PARTIAL"
const val INSTALLMENT_PAID = "PAID"

@Entity(
    tableName = "loan_installments",
    indices = [Index("loanId"), Index("dueEpochDay")],
    foreignKeys = [
        ForeignKey(
            entity = LoanEntity::class,
            parentColumns = ["id"],
            childColumns = ["loanId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LoanInstallmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val loanId: Long,
    val indexNo: Int,
    val dueEpochDay: Long,
    val amountMinor: Long,
    val paidMinor: Long = 0L,
    val status: String = INSTALLMENT_PENDING,
    val paidEpochDay: Long? = null
)

// ------------------------------------------------------------------------ EMI

@Entity(tableName = "emis", indices = [Index("firstDueEpochDay"), Index("archived")])
data class EmiEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val product: String,
    val seller: String? = null,
    val purchaseEpochDay: Long,
    val cashPriceMinor: Long? = null,
    val financedMinor: Long,
    val downPaymentMinor: Long = 0L,
    val totalPayableMinor: Long,
    val installmentAmountMinor: Long,
    val installmentCount: Int,
    val frequency: String,
    val intervalCount: Int = 1,
    val customDays: Int = 0,
    val firstDueEpochDay: Long,
    val finalDueEpochDay: Long? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAtMillis: Long = 0L
)

@Entity(
    tableName = "emi_installments",
    indices = [Index("emiId"), Index("dueEpochDay")],
    foreignKeys = [
        ForeignKey(
            entity = EmiEntity::class,
            parentColumns = ["id"],
            childColumns = ["emiId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EmiInstallmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val emiId: Long,
    val indexNo: Int,
    val dueEpochDay: Long,
    val amountMinor: Long,
    val paidMinor: Long = 0L,
    val status: String = INSTALLMENT_PENDING,
    val paidEpochDay: Long? = null
)

// --------------------------------------------------------- Personal borrowing

@Entity(tableName = "people", indices = [Index("name"), Index("archived")])
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val relationship: String = "Friend",
    val phone: String? = null,
    val email: String? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAtMillis: Long = 0L
)

@Entity(
    tableName = "borrowings",
    indices = [Index("personId"), Index("dueEpochDay")],
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BorrowingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val personId: Long,
    val amountMinor: Long,
    val borrowedEpochDay: Long,
    val dueEpochDay: Long? = null,
    val reason: String? = null,
    val note: String? = null,
    val archived: Boolean = false,
    val createdAtMillis: Long = 0L
)

// ------------------------------------------------------------------- Payments

/**
 * One table for every kind of repayment: shop payments, loan instalments,
 * EMI instalments and personal repayments.
 */
@Entity(
    tableName = "payments",
    indices = [Index("debtType"), Index("debtId"), Index("dateEpochDay")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val debtType: String,
    val debtId: Long,
    val installmentId: Long? = null,
    val amountMinor: Long,
    val dateEpochDay: Long,
    val method: String? = null,
    val note: String? = null,
    val createdAtMillis: Long = 0L
)

// --------------------------------------------------------------------- Ledger

@Entity(tableName = "income", indices = [Index("dateEpochDay"), Index("category")])
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val amountMinor: Long,
    val dateEpochDay: Long,
    val source: String,
    val category: String,
    val description: String? = null,
    val note: String? = null,
    val createdAtMillis: Long = 0L
)

@Entity(tableName = "expenses", indices = [Index("dateEpochDay"), Index("category")])
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val amountMinor: Long,
    val dateEpochDay: Long,
    val category: String,
    val merchant: String? = null,
    val description: String? = null,
    val note: String? = null,
    val createdAtMillis: Long = 0L
)

@Entity(
    tableName = "transactions",
    indices = [Index("dateEpochDay"), Index("type"), Index("relatedType"), Index("relatedId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val type: String,
    val amountMinor: Long,
    val dateEpochDay: Long,
    val dateTimeMillis: Long,
    val title: String,
    val subtitle: String? = null,
    val category: String? = null,
    val relatedType: String? = null,
    val relatedId: Long? = null,
    val note: String? = null
)

@Entity(
    tableName = "categories",
    indices = [Index(value = ["kind", "name"], unique = true)]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val kind: String,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0
)

@Entity(tableName = "reminders", indices = [Index("dueEpochDay"), Index(value = ["debtType", "debtId"])])
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val debtType: String,
    val debtId: Long,
    val dueEpochDay: Long,
    val amountMinor: Long,
    val title: String,
    val notifiedEpochDay: Long? = null
)

// ------------------------------------------------------------ Aggregate rows

/** Two column aggregate rows returned by `GROUP BY` queries. */
data class IdAmountRow(val id: Long, val amountMinor: Long)

data class IdCountRow(val id: Long, val count: Int)

data class IdDayRow(val id: Long, val day: Long)
