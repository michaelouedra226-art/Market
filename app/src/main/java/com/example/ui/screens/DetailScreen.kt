package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.BoutiqueEntity
import com.example.data.database.ProductEntity
import com.example.ui.PulseViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.PulseButton
import com.example.ui.components.PulseImage
import com.example.ui.theme.*

@Composable
fun DetailScreen(viewModel: PulseViewModel) {
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val selectedBoutique by viewModel.selectedBoutique.collectAsState()
    val activeChatPartner by viewModel.activeChatPartner.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Render either Product Details or Boutique Profile
        when {
            selectedProduct != null -> {
                ProductDetailsView(
                    product = selectedProduct!!,
                    viewModel = viewModel,
                    onBack = { viewModel.selectProduct(null) }
                )
            }
            selectedBoutique != null -> {
                BoutiqueProfileView(
                    boutique = selectedBoutique!!,
                    viewModel = viewModel,
                    onBack = { viewModel.selectBoutique(null) }
                )
            }
        }

        // --- In-App Floating Chat Dialog Overlay ---
        if (activeChatPartner != null) {
            ChatDialogOverlay(
                partner = activeChatPartner!!,
                viewModel = viewModel,
                onDismiss = { viewModel.closeChat() }
            )
        }
    }
}

@Composable
fun ProductDetailsView(
    product: ProductEntity,
    viewModel: PulseViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val boutiques by viewModel.boutiques.collectAsState()
    val matchingBoutique = boutiques.find { it.id == product.boutiqueId }

    var selectedDetailTab by remember { mutableStateOf("Description") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Custom Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(TextSecondary.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Text("Pulse Détails", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            IconButton(
                onClick = { viewModel.toggleFavorite(product) },
                modifier = Modifier.background(TextSecondary.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(
                    imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (product.isFavorite) PromoRed else TextPrimary
                )
            }
        }

        // Core Contents
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // space for fixed bottom bar
        ) {
            // Media Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(26.dp))
            ) {
                PulseImage(
                    nameOrUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (product.badgeText != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(PromoRed, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            product.badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pricing & Ratings Row
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (product.oldPrice != null) {
                            Text(
                                text = "${product.oldPrice.toInt()} F",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                        Text(
                            text = "${product.price.toInt()} FCFA",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricBlue
                        )
                    }

                    // Stock indicators
                    Box(
                        modifier = Modifier
                            .background(
                                if (product.stockCount <= 5) PromoRed.copy(alpha = 0.08f) else SuccessGreen.copy(alpha = 0.08f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (product.stockCount <= 5) "Stock faible (${product.stockCount})" else "En Stock (${product.stockCount})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (product.stockCount <= 5) PromoRed else SuccessGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = product.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                // Rating & Category
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                    Text(
                        text = " ${product.rating}  •  ${product.salesCount} commandes",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .background(PremiumViolet.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(product.category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PremiumViolet)
                    }
                }

                // Vendor Quick view
                if (matchingBoutique != null) {
                    Divider(color = TextSecondary.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectBoutique(matchingBoutique) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        ) {
                            PulseImage(nameOrUrl = matchingBoutique.logoUrl, contentDescription = matchingBoutique.name, modifier = Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(matchingBoutique.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                if (matchingBoutique.isVerified) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp).padding(start = 2.dp))
                                }
                            }
                            Text("Boutique vérifiée  •  Délai réponse: ~${matchingBoutique.responseTime}", fontSize = 11.sp, color = TextSecondary)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextSecondary)
                    }
                    Divider(color = TextSecondary.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 12.dp))
                }

                // Dynamic tabs (Description, Shipping, Warranty)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val tabs = listOf("Description", "Livraison", "Garantie")
                    for (tab in tabs) {
                        val isSel = selectedDetailTab == tab
                        val txtCol = if (isSel) ElectricBlue else TextSecondary
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDetailTab = tab }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(tab, fontSize = 13.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium, color = txtCol)
                                if (isSel) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .width(24.dp)
                                            .height(2.dp)
                                            .background(ElectricBlue, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Content Render
                Text(
                    text = when (selectedDetailTab) {
                        "Livraison" -> "📦 **Livraison Express en 24h/48h** sur toute l'Afrique sub-saharienne. Remise en main propre contre paiement ou paiement mobile.\n\nFrais de livraison fixes : 1 500 FCFA à Dakar et Abidjan, 2 500 FCFA dans les autres régions."
                        "Garantie" -> "🛡 **Satisfait ou Remboursé** sous 7 jours après réception de la marchandise.\n\nGarantie constructeur de 6 mois sur l'électronique de luxe."
                        else -> product.description
                    },
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // --- FIXED BOTTOM ACTION SHEET BAR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                .padding(vertical = 12.dp, horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Chat button
                OutlinedButton(
                    onClick = {
                        if (matchingBoutique != null) {
                            viewModel.openChatWith(matchingBoutique)
                        }
                    },
                    modifier = Modifier
                        .size(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "In-App Chat", tint = ElectricBlue)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // WhatsApp Contact
                OutlinedButton(
                    onClick = {
                        if (matchingBoutique != null) {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/${matchingBoutique.whatsapp}?text=Bonjour, je suis intéressé par votre produit '${product.name}' sur Pulse Market.")
                            }
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SuccessGreen),
                    border = BorderStroke(1.5.dp, SuccessGreen)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🟢 WhatsApp", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Buy Direct Button
                PulseButton(
                    text = "Acheter Direct",
                    onClick = {
                        // Directly triggers standard order checkout
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${matchingBoutique?.phone ?: "+221330000001"}")
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .weight(1f),
                    containerColor = ElectricBlue
                )
            }
        }
    }
}

@Composable
fun BoutiqueProfileView(
    boutique: BoutiqueEntity,
    viewModel: PulseViewModel,
    onBack: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val boutiqueProducts = products.filter { it.boutiqueId == boutique.id }

    Column(modifier = Modifier.fillMaxSize()) {
        // Banner Header view
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            PulseImage(nameOrUrl = boutique.bannerPath, contentDescription = null, modifier = Modifier.fillMaxSize())
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            // Overlays
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Box(
                    modifier = Modifier
                        .background(if (boutique.isFollowed) SuccessGreen else ElectricBlue, RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleFollowBoutique(boutique) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (boutique.isFollowed) "Suivi" else "Suivre",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Shop Description block
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-24).dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                        .background(Color.White)
                ) {
                    PulseImage(nameOrUrl = boutique.logoUrl, contentDescription = boutique.name, modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.padding(bottom = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(boutique.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        if (boutique.isVerified) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = SuccessGreen, modifier = Modifier.size(16.dp).padding(start = 2.dp))
                        }
                    }
                    Text("📍 ${boutique.address}", fontSize = 12.sp, color = TextSecondary)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = boutique.description,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )

                // Ratings and Response time Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .background(TextSecondary.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Score", fontSize = 10.sp, color = TextSecondary)
                        Text("⭐ ${boutique.rating}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ventes", fontSize = 10.sp, color = TextSecondary)
                        Text("${boutique.salesCount}+", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Réponse", fontSize = 10.sp, color = TextSecondary)
                        Text("~${boutique.responseTime}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                    }
                }

                Text(
                    text = "Catalogue de la boutique (${boutiqueProducts.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Render grid
                val chunks = boutiqueProducts.chunked(2)
                for (chunk in chunks) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (product in chunk) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(
                                    product = product,
                                    onClick = { viewModel.selectProduct(product) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(product) }
                                )
                            }
                        }
                        if (chunk.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatDialogOverlay(
    partner: BoutiqueEntity,
    viewModel: PulseViewModel,
    onDismiss: () -> Unit
) {
    val messages by viewModel.getMessagesWithPartner(partner.id).collectAsState(initial = emptyList())
    var textMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        GlassyCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .clickable(enabled = false) {},
            cornerRadius = 28.dp,
            shadowElevation = 16.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        ) {
                            PulseImage(nameOrUrl = partner.logoUrl, contentDescription = partner.name, modifier = Modifier.fillMaxSize())
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(partner.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                if (partner.isVerified) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(12.dp).padding(start = 2.dp))
                                }
                            }
                            Text("Actif  •  Réponse moyenne: ${partner.responseTime}", fontSize = 10.sp, color = TextSecondary)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Divider(color = TextSecondary.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                // Messages Container
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(messages) { msg ->
                        val isUser = msg.senderId == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 0.dp,
                                            bottomEnd = if (isUser) 0.dp else 16.dp
                                        )
                                    )
                                    .background(if (isUser) ElectricBlue else TextSecondary.copy(alpha = 0.08f))
                                    .padding(12.dp)
                                    .widthIn(max = 260.dp)
                            ) {
                                Text(
                                    text = msg.message,
                                    color = if (isUser) Color.White else TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                // Input bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textMessage,
                        onValueChange = { textMessage = it },
                        placeholder = { Text("Écrivez votre message...") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElectricBlue)
                            .clickable {
                                if (textMessage.trim().isNotEmpty()) {
                                    viewModel.sendChatMessage(partner.id, textMessage)
                                    textMessage = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}
