package com.shohankhan.ledgerly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shohankhan.ledgerly.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payments ORDER BY dateEpochDay DESC, id DESC")
    fun observeAll(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE debtType = :type AND debtId = :id ORDER BY dateEpochDay DESC, id DESC")
    fun observeForDebt(type: String, id: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE id = :id")
    suspend fun get(id: Long): PaymentEntity?

    @Insert
    suspend fun insert(entity: PaymentEntity): Long

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COALESCE(SUM(amountMinor), 0) FROM payments WHERE dateEpochDay BETWEEN :from AND :to")
    fun observePaidBetween(from: Long, to: Long): Flow<Long>
}
