package com.shohankhan.ledgerly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shohankhan.ledgerly.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY dueEpochDay ASC")
    fun observeAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE dueEpochDay <= :today ORDER BY dueEpochDay ASC")
    suspend fun dueUpTo(today: Long): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE debtType = :type AND debtId = :id")
    suspend fun forDebt(type: String, id: Long): List<ReminderEntity>

    @Insert
    suspend fun insert(entity: ReminderEntity): Long

    @Insert
    suspend fun insertAll(entities: List<ReminderEntity>)

    @Query("UPDATE reminders SET notifiedEpochDay = :day WHERE id = :id")
    suspend fun markNotified(id: Long, day: Long)

    @Query("DELETE FROM reminders WHERE debtType = :type AND debtId = :id")
    suspend fun deleteForDebt(type: String, id: Long)

    @Query("DELETE FROM reminders")
    suspend fun clear()
}
