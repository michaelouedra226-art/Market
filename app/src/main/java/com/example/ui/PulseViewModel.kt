package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.database.AppDatabase
import com.example.data.database.BoutiqueEntity
import com.example.data.database.ChatMessageEntity
import com.example.data.database.NotificationEntity
import com.example.data.database.ProductEntity
import com.example.data.repository.PulseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class PulseTab {
    ACCUEIL, EXPLORER, PUBLIER, FAVORIS, PROFIL
}

data class ProductDraft(
    val title: String = "",
    val description: String = "",
    val category: String = "High-Tech",
    val price: Double = 0.0,
    val oldPrice: Double? = null,
    val stockCount: Int = 10,
    val imageUrl: String = "img_splash_logo",
    val isFlashSale: Boolean = false,
    val optimizedTitle: String = "",
    val optimizedDescription: String = "",
    val optimizedTags: String = "",
    val optimizedPriceAdvice: String = "",
    val isOptimizing: Boolean = false
)

class PulseViewModel(application: Application, private val repository: PulseRepository) : AndroidViewModel(application) {

    // Bottom Navigation tab
    private val _currentTab = MutableStateFlow(PulseTab.ACCUEIL)
    val currentTab: StateFlow<PulseTab> = _currentTab.asStateFlow()

    // Screen navigation stack overrides (for details)
    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    private val _selectedBoutique = MutableStateFlow<BoutiqueEntity?>(null)
    val selectedBoutique: StateFlow<BoutiqueEntity?> = _selectedBoutique.asStateFlow()

    // Profile Screen Toggle: Client (false) or Vendor (true)
    private val _isSellerMode = MutableStateFlow(false)
    val isSellerMode: StateFlow<Boolean> = _isSellerMode.asStateFlow()

    // Search and AI Search states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _aiResponse = MutableStateFlow<String?>(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Chat states
    private val _activeChatPartner = MutableStateFlow<BoutiqueEntity?>(null)
    val activeChatPartner: StateFlow<BoutiqueEntity?> = _activeChatPartner.asStateFlow()

    // Product Publication Step (1 to 4)
    private val _publishStep = MutableStateFlow(1)
    val publishStep: StateFlow<Int> = _publishStep.asStateFlow()

    private val _productDraft = MutableStateFlow(ProductDraft())
    val productDraft: StateFlow<ProductDraft> = _productDraft.asStateFlow()

    // Database Reactive Flows
    val boutiques: StateFlow<List<BoutiqueEntity>> = repository.allBoutiques
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteProducts: StateFlow<List<ProductEntity>> = repository.favoriteProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    fun selectTab(tab: PulseTab) {
        _currentTab.value = tab
        _selectedProduct.value = null
        _selectedBoutique.value = null
        _activeChatPartner.value = null
    }

    fun selectProduct(product: ProductEntity?) {
        _selectedProduct.value = product
        _selectedBoutique.value = null
    }

    fun selectBoutique(boutique: BoutiqueEntity?) {
        _selectedBoutique.value = boutique
        _selectedProduct.value = null
    }

    fun setSellerMode(enabled: Boolean) {
        _isSellerMode.value = enabled
    }

    fun toggleFavorite(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(product.id, !product.isFavorite)
        }
    }

    fun toggleFollowBoutique(boutique: BoutiqueEntity) {
        viewModelScope.launch {
            repository.updateFollowStatus(boutique.id, !boutique.isFollowed)
        }
    }

    // AI Shopping Assistant search
    fun triggerAiSearch(query: String) {
        _searchQuery.value = query
        if (query.trim().isEmpty()) {
            _aiResponse.value = null
            return
        }
        viewModelScope.launch {
            _isAiLoading.value = true
            val prompt = "Je cherche un produit dans la marketplace Pulse Market : '$query'. " +
                    "Propose les meilleures alternatives de notre catalogue en donnant les prix en FCFA, les avantages, et compare brièvement si nécessaire."
            val instruction = "Tu es l'assistant de recherche intelligent de la marketplace Pulse Market. Réponds en français de manière élégante, concise et structurée, avec du formatage markdown agréable et scannable."
            
            val result = GeminiClient.generateResponse(prompt, instruction)
            _aiResponse.value = result
            _isAiLoading.value = false
        }
    }

    fun clearAiSearch() {
        _searchQuery.value = ""
        _aiResponse.value = null
    }

    // Chat Messaging
    fun openChatWith(boutique: BoutiqueEntity) {
        _activeChatPartner.value = boutique
    }

    fun closeChat() {
        _activeChatPartner.value = null
    }

    fun getMessagesWithPartner(partnerId: String): Flow<List<ChatMessageEntity>> {
        return repository.getMessagesWithPartner(partnerId)
    }

    fun sendChatMessage(partnerId: String, text: String) {
        if (text.trim().isEmpty()) return
        viewModelScope.launch {
            val userMsg = ChatMessageEntity(
                senderId = "user",
                receiverId = partnerId,
                message = text
            )
            repository.insertMessage(userMsg)

            // Simulate quick vendor reply via Gemini / Smart AI
            _isAiLoading.value = true
            val prompt = "Le client vient de m'envoyer ce message dans ma boutique : '$text'. Réponds-lui de manière professionnelle, polie et vendeuse en moins de deux phrases en tant que gérant de boutique."
            val replyText = GeminiClient.generateResponse(prompt, "Tu es le gérant d'une boutique haut de gamme sur la marketplace Pulse Market. Réponds de façon polie, chaleureuse et professionnelle.")
            
            val shopReply = ChatMessageEntity(
                senderId = partnerId,
                receiverId = "user",
                message = replyText
            )
            repository.insertMessage(shopReply)
            _isAiLoading.value = false
        }
    }

    // Step-by-step Publication Draft Management
    fun setPublishStep(step: Int) {
        _publishStep.value = step
    }

    fun updateDraftTitle(title: String) {
        _productDraft.value = _productDraft.value.copy(title = title)
    }

    fun updateDraftDescription(desc: String) {
        _productDraft.value = _productDraft.value.copy(description = desc)
    }

    fun updateDraftCategory(cat: String) {
        _productDraft.value = _productDraft.value.copy(category = cat)
    }

    fun updateDraftPrice(price: Double) {
        _productDraft.value = _productDraft.value.copy(price = price)
    }

    fun updateDraftOldPrice(oldPrice: Double?) {
        _productDraft.value = _productDraft.value.copy(oldPrice = oldPrice)
    }

    fun updateDraftStock(stock: Int) {
        _productDraft.value = _productDraft.value.copy(stockCount = stock)
    }

    fun updateDraftImage(img: String) {
        _productDraft.value = _productDraft.value.copy(imageUrl = img)
    }

    fun updateDraftFlash(flash: Boolean) {
        _productDraft.value = _productDraft.value.copy(isFlashSale = flash)
    }

    // AI Copywriter Optimization for Vendors
    fun optimizeProductWithAI() {
        val draft = _productDraft.value
        if (draft.title.isEmpty() || draft.description.isEmpty()) return

        _productDraft.value = draft.copy(isOptimizing = true)
        viewModelScope.launch {
            val prompt = "Optimise cette fiche produit pour les vendeurs sur Pulse Market :\n" +
                    "Titre d'origine : ${draft.title}\n" +
                    "Description d'origine : ${draft.description}\n" +
                    "Catégorie : ${draft.category}\n" +
                    "Prix d'origine : ${draft.price} FCFA\n\n" +
                    "Donne un titre accrocheur, une description enrichie et professionnelle, des tags SEO, et un conseil de prix recommandé."

            val systemInstr = "Tu es le 'Pulse AI Copywriter Assistant'. Tu améliores automatiquement les fiches produits des commerçants pour maximiser leurs ventes. Renvoie un texte structuré clair séparé par des balises [TITRE], [DESCRIPTION], [TAGS], et [PRIX] ou rédigé de façon lisible avec ces titres."
            
            val response = GeminiClient.generateResponse(prompt, systemInstr)
            
            // Extract fields or parse
            var titleOpt = draft.title
            var descOpt = draft.description
            var tagsOpt = "#Marketplace #PulseMarket #VendeurPros"
            var priceOpt = "Le prix de ${draft.price} FCFA est parfait pour ce segment."

            try {
                if (response.contains("[TITRE]")) {
                    val parts = response.split("[TITRE]", "[DESCRIPTION]", "[TAGS]", "[PRIX]")
                    if (parts.size >= 5) {
                        titleOpt = parts[1].trim()
                        descOpt = parts[2].trim()
                        tagsOpt = parts[3].trim()
                        priceOpt = parts[4].trim()
                    } else {
                        // generic parse
                        descOpt = response
                    }
                } else {
                    descOpt = response
                }
            } catch (e: Exception) {
                descOpt = response
            }

            _productDraft.value = _productDraft.value.copy(
                optimizedTitle = titleOpt,
                optimizedDescription = descOpt,
                optimizedTags = tagsOpt,
                optimizedPriceAdvice = priceOpt,
                isOptimizing = false
            )
        }
    }

    fun commitPublishDraft() {
        val draft = _productDraft.value
        val titleFinal = if (draft.optimizedTitle.isNotEmpty()) draft.optimizedTitle else draft.title
        val descFinal = if (draft.optimizedDescription.isNotEmpty()) draft.optimizedDescription else draft.description
        
        val newProduct = ProductEntity(
            id = "p_user_" + System.currentTimeMillis(),
            name = titleFinal,
            description = descFinal,
            price = draft.price,
            oldPrice = draft.oldPrice,
            category = draft.category,
            imageUrl = draft.imageUrl,
            rating = 5.0,
            salesCount = 0,
            stockCount = draft.stockCount,
            boutiqueId = "b1", // put under default boutique 1 (Luxury Time)
            badgeText = if (draft.isFlashSale) "Flash Sale" else "Nouveau",
            isFavorite = false,
            isPromoted = false,
            isFlashSale = draft.isFlashSale
        )

        viewModelScope.launch {
            repository.insertProduct(newProduct)
            
            // Send system notification
            val notif = NotificationEntity(
                title = "Produit Publié ! 🎉",
                content = "Votre produit '$titleFinal' est maintenant en ligne et visible par des milliers d'acheteurs.",
                type = "stock"
            )
            repository.insertNotification(notif)
            
            // Reset state
            _productDraft.value = ProductDraft()
            _publishStep.value = 1
            selectTab(PulseTab.ACCUEIL)
        }
    }

    fun dismissNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }
}

class PulseViewModelFactory(private val application: Application, private val repository: PulseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PulseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PulseViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
