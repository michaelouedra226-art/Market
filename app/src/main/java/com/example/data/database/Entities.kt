package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boutiques")
data class BoutiqueEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val bannerPath: String, // can be a drawable resource string or local path
    val logoUrl: String, // drawable name or url
    val address: String,
    val whatsapp: String,
    val phone: String,
    val rating: Double,
    val salesCount: Int,
    val responseTime: String,
    val isVerified: Boolean,
    val isFollowed: Boolean = false
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val oldPrice: Double? = null,
    val category: String,
    val imageUrl: String, // drawable name or url
    val rating: Double,
    val salesCount: Int,
    val stockCount: Int,
    val boutiqueId: String,
    val badgeText: String? = null,
    val isFavorite: Boolean = false,
    val isPromoted: Boolean = false,
    val isFlashSale: Boolean = false
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val type: String, // "stock", "order", "price_drop", "promo"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
