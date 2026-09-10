package com.shohankhan.ledgerly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shohankhan.ledgerly.data.local.entity.EmiEntity
import com.shohankhan.ledgerly.data.local.entity.EmiInstallmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmiDao {

    @Query("SELECT * FROM emis ORDER BY archived ASC, firstDueEpochDay ASC")
    fun observeEmis(): Flow<List<EmiEntity>>

    @Query("SELECT * FROM emis WHERE id = :id")
    fun observeEmi(id: Long): Flow<EmiEntity?>

    @Query("SELECT * FROM emis WHERE id = :id")
    suspend fun getEmi(id: Long): EmiEntity?

    @Insert
    suspend fun insertEmi(entity: EmiEntity): Long

    @Update
    suspend fun updateEmi(entity: EmiEntity)

    @Query("UPDATE emis SET archived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)

    @Query("DELETE FROM emis WHERE id = :id")
    suspend fun deleteEmi(id: Long)

    @Query("SELECT * FROM emi_installments ORDER BY emiId ASC, indexNo ASC")
    fun observeAllInstallments(): Flow<List<EmiInstallmentEntity>>

    @Query("SELECT * FROM emi_installments WHERE emiId = :emiId ORDER BY indexNo ASC")
    fun observeInstallments(emiId: Long): Flow<List<EmiInstallmentEntity>>

    @Query("SELECT * FROM emi_installments WHERE emiId = :emiId ORDER BY indexNo ASC")
    suspend fun getInstallments(emiId: Long): List<EmiInstallmentEntity>

    @Insert
    suspend fun insertInstallments(items: List<EmiInstallmentEntity>)

    @Update
    suspend fun updateInstallment(item: EmiInstallmentEntity)

    @Query("DELETE FROM emi_installments WHERE emiId = :emiId")
    suspend fun deleteInstallments(emiId: Long)
}
