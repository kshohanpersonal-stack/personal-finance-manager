package com.shohankhan.ledgerly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shohankhan.ledgerly.data.local.entity.BorrowingEntity
import com.shohankhan.ledgerly.data.local.entity.PersonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Query("SELECT * FROM people ORDER BY archived ASC, name COLLATE NOCASE ASC")
    fun observePeople(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people WHERE id = :id")
    fun observePerson(id: Long): Flow<PersonEntity?>

    @Query("SELECT * FROM people WHERE id = :id")
    suspend fun getPerson(id: Long): PersonEntity?

    @Insert
    suspend fun insertPerson(entity: PersonEntity): Long

    @Update
    suspend fun updatePerson(entity: PersonEntity)

    @Query("UPDATE people SET archived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)

    @Query("DELETE FROM people WHERE id = :id")
    suspend fun deletePerson(id: Long)

    @Query("SELECT * FROM borrowings ORDER BY borrowedEpochDay DESC, id DESC")
    fun observeAllBorrowings(): Flow<List<BorrowingEntity>>

    @Query("SELECT * FROM borrowings WHERE personId = :personId ORDER BY borrowedEpochDay DESC, id DESC")
    fun observeBorrowings(personId: Long): Flow<List<BorrowingEntity>>

    @Query("SELECT * FROM borrowings WHERE id = :id")
    suspend fun getBorrowing(id: Long): BorrowingEntity?

    @Insert
    suspend fun insertBorrowing(entity: BorrowingEntity): Long

    @Update
    suspend fun updateBorrowing(entity: BorrowingEntity)

    @Query("UPDATE borrowings SET archived = :archived WHERE id = :id")
    suspend fun setBorrowingArchived(id: Long, archived: Boolean)

    @Query("DELETE FROM borrowings WHERE id = :id")
    suspend fun deleteBorrowing(id: Long)
}
