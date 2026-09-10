package com.shohankhan.ledgerly.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.shohankhan.ledgerly.data.local.entity.ShopEntity
import com.shohankhan.ledgerly.data.local.entity.ShopPurchaseEntity
import com.shohankhan.ledgerly.data.local.entity.ShopPurchaseItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    @Query("SELECT * FROM shops ORDER BY archived ASC, name COLLATE NOCASE ASC")
    fun observeShops(): Flow<List<ShopEntity>>

    @Query("SELECT * FROM shops WHERE id = :id")
    fun observeShop(id: Long): Flow<ShopEntity?>

    @Query("SELECT * FROM shops WHERE id = :id")
    suspend fun getShop(id: Long): ShopEntity?

    @Insert
    suspend fun insertShop(entity: ShopEntity): Long

    @Update
    suspend fun updateShop(entity: ShopEntity)

    @Query("UPDATE shops SET archived = :archived WHERE id = :id")
    suspend fun setArchived(id: Long, archived: Boolean)

    @Query("DELETE FROM shops WHERE id = :id")
    suspend fun deleteShop(id: Long)

    @Query("SELECT * FROM shop_purchases ORDER BY dateEpochDay DESC, id DESC")
    fun observeAllPurchases(): Flow<List<ShopPurchaseEntity>>

    @Query("SELECT * FROM shop_purchases WHERE shopId = :shopId ORDER BY dateEpochDay DESC, id DESC")
    fun observePurchases(shopId: Long): Flow<List<ShopPurchaseEntity>>

    @Query("SELECT * FROM shop_purchases WHERE id = :id")
    suspend fun getPurchase(id: Long): ShopPurchaseEntity?

    @Query("SELECT * FROM shop_purchase_items ORDER BY id ASC")
    fun observeAllItems(): Flow<List<ShopPurchaseItemEntity>>

    @Query("SELECT * FROM shop_purchase_items WHERE purchaseId = :purchaseId ORDER BY id ASC")
    fun observeItems(purchaseId: Long): Flow<List<ShopPurchaseItemEntity>>

    @Insert
    suspend fun insertPurchase(entity: ShopPurchaseEntity): Long

    @Update
    suspend fun updatePurchase(entity: ShopPurchaseEntity)

    @Insert
    suspend fun insertItems(items: List<ShopPurchaseItemEntity>)

    @Query("DELETE FROM shop_purchase_items WHERE purchaseId = :purchaseId")
    suspend fun deleteItems(purchaseId: Long)

    @Query("DELETE FROM shop_purchases WHERE id = :id")
    suspend fun deletePurchase(id: Long)
}
