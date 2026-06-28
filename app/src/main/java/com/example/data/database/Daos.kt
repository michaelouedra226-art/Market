package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BoutiqueDao {
    @Query("SELECT * FROM boutiques")
    fun getAllBoutiques(): Flow<List<BoutiqueEntity>>

    @Query("SELECT * FROM boutiques WHERE id = :id")
    fun getBoutiqueById(id: String): Flow<BoutiqueEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoutique(boutique: BoutiqueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoutiques(boutiques: List<BoutiqueEntity>)

    @Query("UPDATE boutiques SET isFollowed = :isFollowed WHERE id = :id")
    suspend fun updateFollowStatus(id: String, isFollowed: Boolean)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE boutiqueId = :boutiqueId")
    fun getProductsByBoutique(boutiqueId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isFavorite = 1")
    fun getFavoriteProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("UPDATE products SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFav: Boolean)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE senderId = :partnerId OR receiverId = :partnerId ORDER BY timestamp ASC")
    fun getMessagesWithPartner(partnerId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)
}
