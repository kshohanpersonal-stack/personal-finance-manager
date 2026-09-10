package com.shohankhan.ledgerly.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shohankhan.ledgerly.data.local.dao.EmiDao
import com.shohankhan.ledgerly.data.local.dao.LedgerDao
import com.shohankhan.ledgerly.data.local.dao.LoanDao
import com.shohankhan.ledgerly.data.local.dao.PaymentDao
import com.shohankhan.ledgerly.data.local.dao.PersonDao
import com.shohankhan.ledgerly.data.local.dao.ProfileDao
import com.shohankhan.ledgerly.data.local.dao.ReminderDao
import com.shohankhan.ledgerly.data.local.dao.ShopDao
import com.shohankhan.ledgerly.data.local.entity.BorrowingEntity
import com.shohankhan.ledgerly.data.local.entity.CategoryEntity
import com.shohankhan.ledgerly.data.local.entity.EmiEntity
import com.shohankhan.ledgerly.data.local.entity.EmiInstallmentEntity
import com.shohankhan.ledgerly.data.local.entity.ExpenseEntity
import com.shohankhan.ledgerly.data.local.entity.IncomeEntity
import com.shohankhan.ledgerly.data.local.entity.LoanEntity
import com.shohankhan.ledgerly.data.local.entity.LoanInstallmentEntity
import com.shohankhan.ledgerly.data.local.entity.PaymentEntity
import com.shohankhan.ledgerly.data.local.entity.PersonEntity
import com.shohankhan.ledgerly.data.local.entity.ReminderEntity
import com.shohankhan.ledgerly.data.local.entity.ShopEntity
import com.shohankhan.ledgerly.data.local.entity.ShopPurchaseEntity
import com.shohankhan.ledgerly.data.local.entity.ShopPurchaseItemEntity
import com.shohankhan.ledgerly.data.local.entity.TransactionEntity
import com.shohankhan.ledgerly.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        ShopEntity::class,
        ShopPurchaseEntity::class,
        ShopPurchaseItemEntity::class,
        LoanEntity::class,
        LoanInstallmentEntity::class,
        EmiEntity::class,
        EmiInstallmentEntity::class,
        PersonEntity::class,
        BorrowingEntity::class,
        PaymentEntity::class,
        IncomeEntity::class,
        ExpenseEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LedgerlyDatabase : RoomDatabase() {

    abstract fun shopDao(): ShopDao
    abstract fun loanDao(): LoanDao
    abstract fun emiDao(): EmiDao
    abstract fun personDao(): PersonDao
    abstract fun paymentDao(): PaymentDao
    abstract fun ledgerDao(): LedgerDao
    abstract fun profileDao(): ProfileDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val NAME = "ledgerly.db"

        @Volatile
        private var instance: LedgerlyDatabase? = null

        fun get(context: Context): LedgerlyDatabase =
            instance ?: synchronized(this) {
                instance ?: build(context).also { instance = it }
            }

        private fun build(context: Context): LedgerlyDatabase =
            Room.databaseBuilder(context.applicationContext, LedgerlyDatabase::class.java, NAME)
                .addCallback(SeedCallback())
                .build()

        private class SeedCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                DefaultCategories.income.forEachIndexed { index, name ->
                    db.execSQL(
                        "INSERT OR IGNORE INTO categories (name, kind, isCustom, sortOrder) " +
                            "VALUES ('${name.replace("'", "''")}', 'INCOME', 0, $index)"
                    )
                }
                DefaultCategories.expense.forEachIndexed { index, name ->
                    db.execSQL(
                        "INSERT OR IGNORE INTO categories (name, kind, isCustom, sortOrder) " +
                            "VALUES ('${name.replace("'", "''")}', 'EXPENSE', 0, $index)"
                    )
                }
            }
        }
    }
}

object DefaultCategories {
    val income: List<String> = listOf(
        "Salary", "Business", "Freelance", "Bonus",
        "Commission", "Gift", "Investment", "Other"
    )
    val expense: List<String> = listOf(
        "Food", "Transport", "Shopping", "Bills", "Rent", "Education",
        "Healthcare", "Family", "Entertainment", "Utilities", "Debt payment", "Other"
    )
    val relationships: List<String> = listOf(
        "Friend", "Relative", "Family", "Colleague", "Neighbour", "Other"
    )
    val lenders: List<String> = listOf(
        "Bank", "NGO", "Microfinance", "Cooperative", "Employer", "Other"
    )
}
