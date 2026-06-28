package com.example.data.repository

import android.util.Log
import com.example.data.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class PulseRepository(private val db: AppDatabase) {

    private val boutiqueDao = db.boutiqueDao()
    private val productDao = db.productDao()
    private val chatMessageDao = db.chatMessageDao()
    private val notificationDao = db.notificationDao()

    val allBoutiques: Flow<List<BoutiqueEntity>> = boutiqueDao.getAllBoutiques()
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val favoriteProducts: Flow<List<ProductEntity>> = productDao.getFavoriteProducts()
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val allChatMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()

    fun getBoutiqueById(id: String): Flow<BoutiqueEntity?> = boutiqueDao.getBoutiqueById(id)
    fun getProductById(id: String): Flow<ProductEntity?> = productDao.getProductById(id)
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = productDao.getProductsByCategory(category)
    fun getProductsByBoutique(boutiqueId: String): Flow<List<ProductEntity>> = productDao.getProductsByBoutique(boutiqueId)
    fun getMessagesWithPartner(partnerId: String): Flow<List<ChatMessageEntity>> = chatMessageDao.getMessagesWithPartner(partnerId)

    suspend fun updateFavoriteStatus(productId: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        productDao.updateFavoriteStatus(productId, isFavorite)
    }

    suspend fun updateFollowStatus(boutiqueId: String, isFollowed: Boolean) = withContext(Dispatchers.IO) {
        boutiqueDao.updateFollowStatus(boutiqueId, isFollowed)
    }

    suspend fun insertProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun insertBoutique(boutique: BoutiqueEntity) = withContext(Dispatchers.IO) {
        boutiqueDao.insertBoutique(boutique)
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    suspend fun insertMessage(message: ChatMessageEntity) = withContext(Dispatchers.IO) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun insertNotification(notification: NotificationEntity) = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun deleteNotification(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.deleteNotification(id)
    }

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val existingBoutiques = boutiqueDao.getAllBoutiques().first()
        if (existingBoutiques.isEmpty()) {
            Log.d("PulseRepository", "Database is empty. Populating with premium mock data...")
            
            val mockBoutiques = listOf(
                BoutiqueEntity(
                    id = "b1",
                    name = "Luxury Time Dakar",
                    description = "Boutique d'horlogerie de luxe et bijoux de haute joaillerie. Nous proposons les plus grandes marques avec garantie internationale et certificat d'authenticité.",
                    bannerPath = "img_boutique_banner",
                    logoUrl = "img_splash_logo",
                    address = "Avenue Cheikh Anta Diop, Dakar, Sénégal",
                    whatsapp = "+221770000001",
                    phone = "+221330000001",
                    rating = 4.9,
                    salesCount = 1540,
                    responseTime = "5 min",
                    isVerified = true,
                    isFollowed = true
                ),
                BoutiqueEntity(
                    id = "b2",
                    name = "StreetWear Hub Abidjan",
                    description = "La référence de la culture streetwear en Afrique de l'Ouest. Sneakers en édition limitée, vêtements urbains stylés et accessoires de créateurs indépendants.",
                    bannerPath = "img_onboarding_hero",
                    logoUrl = "img_onboarding_hero",
                    address = "Zone 4, Boulevard de Marseille, Abidjan, Côte d'Ivoire",
                    whatsapp = "+2250700000002",
                    phone = "+2252100000002",
                    rating = 4.7,
                    salesCount = 980,
                    responseTime = "12 min",
                    isVerified = true,
                    isFollowed = false
                ),
                BoutiqueEntity(
                    id = "b3",
                    name = "Tech Zone",
                    description = "Votre espace high-tech préféré pour les smartphones haut de gamme, ordinateurs de dernière génération, casques audio de salon et accessoires connectés.",
                    bannerPath = "img_boutique_banner",
                    logoUrl = "img_boutique_banner",
                    address = "Proche de l'Université de Ouagadougou, Ouagadougou, Burkina Faso",
                    whatsapp = "+22670000003",
                    phone = "+22625000003",
                    rating = 4.8,
                    salesCount = 2450,
                    responseTime = "15 min",
                    isVerified = true,
                    isFollowed = false
                ),
                BoutiqueEntity(
                    id = "b4",
                    name = "Kora Store Mali",
                    description = "Fournisseur de gadgets intelligents et petits appareils électroménagers innovants pour simplifier votre quotidien et moderniser votre maison.",
                    bannerPath = "img_onboarding_hero",
                    logoUrl = "img_splash_logo",
                    address = "Quartier du Fleuve, Bamako, Mali",
                    whatsapp = "+22370000004",
                    phone = "+22320000004",
                    rating = 4.2,
                    salesCount = 450,
                    responseTime = "25 min",
                    isVerified = false,
                    isFollowed = false
                )
            )
            boutiqueDao.insertBoutiques(mockBoutiques)

            val mockProducts = listOf(
                ProductEntity(
                    id = "p1",
                    name = "Montre Quartz Chronographe",
                    description = "Une montre élégante et sportive étanche à quartz avec chronographe actif, bracelet en cuir noir véritable surpiqué et verre saphir inrayable. Parfaite pour s'affirmer au quotidien ou en réunion professionnelle.",
                    price = 12500.0,
                    oldPrice = 18000.0,
                    category = "Horlogerie",
                    imageUrl = "img_splash_logo",
                    rating = 4.7,
                    salesCount = 120,
                    stockCount = 15,
                    boutiqueId = "b1",
                    badgeText = "Vente Flash",
                    isFavorite = true,
                    isPromoted = true,
                    isFlashSale = true
                ),
                ProductEntity(
                    id = "p2",
                    name = "Sneakers Pulse Sport Pro",
                    description = "Sneakers de running ultra-légères. Semelle intermédiaire en mousse réactive, empeigne respirante en mesh tricoté et ajustement parfait. Conçues pour repousser vos limites.",
                    price = 24500.0,
                    oldPrice = 29000.0,
                    category = "Mode",
                    imageUrl = "img_onboarding_hero",
                    rating = 4.5,
                    salesCount = 85,
                    stockCount = 8,
                    boutiqueId = "b2",
                    badgeText = "-15%",
                    isFavorite = false,
                    isPromoted = true
                ),
                ProductEntity(
                    id = "p3",
                    name = "Smart Watch Active 3",
                    description = "Suivez votre activité physique au plus près. Mesure continue du rythme cardiaque, niveau d'oxygène dans le sang, suivi de sommeil avancé et réception des notifications WhatsApp et appels.",
                    price = 14000.0,
                    oldPrice = null,
                    category = "Horlogerie",
                    imageUrl = "img_splash_logo",
                    rating = 4.2,
                    salesCount = 64,
                    stockCount = 22,
                    boutiqueId = "b1",
                    badgeText = "Nouveau",
                    isFavorite = false
                ),
                ProductEntity(
                    id = "p4",
                    name = "iPhone 13 Pro Max - 128Go",
                    description = "Smartphone haut de gamme état irréprochable (Grade A+), santé batterie à 94%. Vendu avec chargeur rapide et film de protection offert. Garantie contractuelle de 6 mois pour votre sérénité.",
                    price = 380000.0,
                    oldPrice = 420000.0,
                    category = "High-Tech",
                    imageUrl = "img_boutique_banner",
                    rating = 4.9,
                    salesCount = 310,
                    stockCount = 4,
                    boutiqueId = "b3",
                    badgeText = "Garantie 6 mois",
                    isFavorite = true,
                    isPromoted = true
                ),
                ProductEntity(
                    id = "p5",
                    name = "Casque Pulse Bass Boost ANC",
                    description = "Évadez-vous dans votre musique. Casque sans-fil circum-auriculaire équipé d'une réduction active du bruit de niveau professionnel, basses profondes réglables et charge ultra-rapide USB-C.",
                    price = 18500.0,
                    oldPrice = 22000.0,
                    category = "High-Tech",
                    imageUrl = "img_onboarding_hero",
                    rating = 4.6,
                    salesCount = 42,
                    stockCount = 12,
                    boutiqueId = "b3",
                    badgeText = "Flash -15%",
                    isFavorite = false,
                    isPromoted = false,
                    isFlashSale = true
                )
            )
            productDao.insertProducts(mockProducts)

            val mockNotifications = listOf(
                NotificationEntity(
                    title = "Bienvenue sur Pulse Market !",
                    content = "Découvrez la marketplace nouvelle génération. Achetez, vendez et négociez en toute sécurité.",
                    type = "promo"
                ),
                NotificationEntity(
                    title = "Vente Flash Activée ⚡",
                    content = "La Montre Quartz Chronographe de Luxury Time est actuellement à -30% ! Stock limité.",
                    type = "price_drop"
                )
            )
            for (notif in mockNotifications) {
                notificationDao.insertNotification(notif)
            }

            val mockMessages = listOf(
                ChatMessageEntity(
                    senderId = "b1",
                    receiverId = "user",
                    message = "Bonjour ! Merci d'avoir visité notre boutique Luxury Time. Nous serions ravis de répondre à toutes vos questions sur la Montre Quartz Chronographe."
                ),
                ChatMessageEntity(
                    senderId = "b3",
                    receiverId = "user",
                    message = "Bonjour, nos iPhones sont garantis 6 mois avec remplacement immédiat en cas de panne technique. Êtes-vous intéressé par une livraison ?"
                )
            )
            for (msg in mockMessages) {
                chatMessageDao.insertMessage(msg)
            }
        }
    }
}
