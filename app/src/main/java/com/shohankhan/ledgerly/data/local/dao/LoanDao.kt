package com.shohankhan.ledgerly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shohankhan.ledgerly.data.local.entity.LoanEntity
import com.shohankhan.ledgerly.data.local.entity.LoanInstallmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Query("SELECT * FROM loans ORDER BY archived ASC, firstDueEpochDay ASC")
    fun observeLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE id = :id")
    fun observeLoan(id: Long): Flow<LoanEntity?>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getLoan(id: Long): LoanEntity?

    @Insert
    suspend fun insertLoan(entity: LoanEntity): Long

    @Update
    suspend fun updateLoan(entity: LoanEntity)

    @Query("UPDATE loans SET archived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)

    @Query("DELETE FROM loans WHERE id = :id")
    suspend fun deleteLoan(id: Long)

    @Query("SELECT * FROM loan_installments ORDER BY loanId ASC, indexNo ASC")
    fun observeAllInstallments(): Flow<List<LoanInstallmentEntity>>

    @Query("SELECT * FROM loan_installments WHERE loanId = :loanId ORDER BY indexNo ASC")
    fun observeInstallments(loanId: Long): Flow<List<LoanInstallmentEntity>>

    @Query("SELECT * FROM loan_installments WHERE loanId = :loanId ORDER BY indexNo ASC")
    suspend fun getInstallments(loanId: Long): List<LoanInstallmentEntity>

    @Insert
    suspend fun insertInstallments(items: List<LoanInstallmentEntity>)

    @Update
    suspend fun updateInstallment(item: LoanInstallmentEntity)

    @Query("DELETE FROM loan_installments WHERE loanId = :loanId")
    suspend fun deleteInstallments(loanId: Long)
}
