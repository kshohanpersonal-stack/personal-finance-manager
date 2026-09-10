package com.shohankhan.ledgerly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shohankhan.ledgerly.data.local.entity.CategoryEntity
import com.shohankhan.ledgerly.data.local.entity.ExpenseEntity
import com.shohankhan.ledgerly.data.local.entity.IncomeEntity
import com.shohankhan.ledgerly.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerDao {

    // ------------------------------------------------------------- income

    @Query("SELECT * FROM income ORDER BY dateEpochDay DESC, id DESC")
    fun observeIncome(): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM income WHERE id = :id")
    suspend fun getIncome(id: Long): IncomeEntity?

    @Insert
    suspend fun insertIncome(entity: IncomeEntity): Long

    @Update
    suspend fun updateIncome(entity: IncomeEntity)

    @Query("DELETE FROM income WHERE id = :id")
    suspend fun deleteIncome(id: Long)

    // ------------------------------------------------------------ expense

    @Query("SELECT * FROM expenses ORDER BY dateEpochDay DESC, id DESC")
    fun observeExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpense(id: Long): ExpenseEntity?

    @Insert
    suspend fun insertExpense(entity: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(entity: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: Long)

    // ------------------------------------------------------- transactions

    @Query("SELECT * FROM transactions ORDER BY dateTimeMillis DESC, id DESC")
    fun observeTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateTimeMillis DESC, id DESC LIMIT :limit")
    fun observeRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun observeTransaction(id: Long): Flow<TransactionEntity?>

    @Query("SELECT * FROM transactions WHERE relatedType = :type AND relatedId = :id ORDER BY dateTimeMillis DESC, id DESC")
    fun observeTransactionsFor(type: String, id: Long): Flow<List<TransactionEntity>>

    @Insert
    suspend fun insertTransaction(entity: TransactionEntity): Long

    @Query("DELETE FROM transactions WHERE relatedType = :type AND relatedId = :id")
    suspend fun deleteTransactionsFor(type: String, id: Long)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    // ---------------------------------------------------------- categories

    @Query("SELECT * FROM categories ORDER BY kind ASC, sortOrder ASC, name COLLATE NOCASE ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE kind = :kind ORDER BY sortOrder ASC, name COLLATE NOCASE ASC")
    suspend fun getCategories(kind: String): List<CategoryEntity>

    @Insert
    suspend fun insertCategory(entity: CategoryEntity): Long

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: Long)
}
