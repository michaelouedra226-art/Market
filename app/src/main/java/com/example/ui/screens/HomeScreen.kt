package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.BoutiqueEntity
import com.example.data.database.ProductEntity
import com.example.ui.PulseTab
import com.example.ui.PulseViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.PulseImage
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: PulseViewModel,
    onProductSelected: (ProductEntity) -> Unit,
    onBoutiqueSelected: (BoutiqueEntity) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val boutiques by viewModel.boutiques.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val listState = rememberLazyListState()

    // Shrinking search bar threshold calculation
    val isScrolled = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 60
        }
    }

    val searchBarHeight by animateDpAsState(
        targetValue = if (isScrolled.value) 50.dp else 56.dp,
        label = "sb_height"
    )

    val searchBarPadding by animateDpAsState(
        targetValue = if (isScrolled.value) 12.dp else 16.dp,
        label = "sb_padding"
    )

    val searchBarRadius by animateDpAsState(
        targetValue = if (isScrolled.value) 18.dp else 24.dp,
        label = "sb_radius"
    )

    var showNotificationsDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 90.dp, bottom = 100.dp) // Space for floating search and navigation
        ) {
            // 1. Promo Banner Carousel
            item {
                PromoCarousel()
            }

            // 2. Circular Categories
            item {
                CategorySection()
            }

            // 3. Flash Sales (⚡ Vente Flash)
            val flashSales = products.filter { it.isFlashSale }
            if (flashSales.isNotEmpty()) {
                item {
                    SectionHeader(title = "⚡ Ventes Flash", onSeeAll = {})
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(flashSales) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductSelected(product) },
                                onFavoriteToggle = { viewModel.toggleFavorite(product) }
                            )
                        }
                    }
                }
            }

            // 4. Trends (🔥 Tendances)
            val trending = products.filter { it.isPromoted }
            if (trending.isNotEmpty()) {
                item {
                    SectionHeader(title = "🔥 Tendances du Moment", onSeeAll = {})
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(trending) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductSelected(product) },
                                onFavoriteToggle = { viewModel.toggleFavorite(product) }
                            )
                        }
                    }
                }
            }

            // 5. Popular Boutiques (🏪 Boutiques populaires)
            if (boutiques.isNotEmpty()) {
                item {
                    SectionHeader(title = "🏪 Boutiques Populaires", onSeeAll = {})
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(boutiques) { boutique ->
                            BoutiqueCard(
                                boutique = boutique,
                                onClick = { onBoutiqueSelected(boutique) },
                                onFollowToggle = { viewModel.toggleFollowBoutique(boutique) }
                            )
                        }
                    }
                }
            }

            // 6. Recommended Products (⭐ Recommandés)
            item {
                SectionHeader(title = "⭐ Recommandé Pour Vous", onSeeAll = {})
            }

            val chunkedProducts = products.chunked(2)
            items(chunkedProducts) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (product in rowItems) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductCard(
                                product = product,
                                onClick = { onProductSelected(product) },
                                onFavoriteToggle = { viewModel.toggleFavorite(product) }
                            )
                        }
                    }
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // --- FLOATING FLOATING HEADER (Floating search bar + notification) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = searchBarPadding)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(searchBarHeight)
                    .clip(RoundedCornerShape(searchBarRadius))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        RoundedCornerShape(searchBarRadius)
                    )
                    .clickable { viewModel.selectTab(PulseTab.EXPLORER) }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ElectricBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Rechercher un produit, une montre...",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )

                // Voice mockup button
                IconButton(onClick = { viewModel.selectTab(PulseTab.EXPLORER) }) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Voice",
                        tint = PremiumViolet,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Notification badge button
                Box {
                    IconButton(onClick = { showNotificationsDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = TextPrimary
                        )
                    }
                    val unreadCount = notifications.count { !it.isRead }
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(PromoRed, CircleShape)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // Notifications Dialog Overlay
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Notifications", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (notifications.isEmpty()) {
                        Text(
                            "Aucune notification pour le moment.",
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        for (notif in notifications) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (notif.isRead) Color.Transparent else ElectricBlue.copy(
                                            alpha = 0.05f
                                        ),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = notif.content,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                IconButton(onClick = { viewModel.dismissNotification(notif.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Fermer", color = ElectricBlue)
                }
            }
        )
    }
}

@Composable
fun PromoCarousel() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(ElectricBlue, PremiumViolet)
                )
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        PulseImage(
            nameOrUrl = "img_boutique_banner",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Dark translucent overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(PromoRed, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "PROMO FLASH",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
            Text(
                "Jusqu'à -30% Tech & Luxe",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "Livraison express à domicile",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun CategorySection() {
    val categories = listOf(
        Pair("Horlogerie", "⌚"),
        Pair("Mode", "👕"),
        Pair("High-Tech", "💻"),
        Pair("Maison", "🏠"),
        Pair("Bijoux", "💍")
    )

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = "Catégories",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(categories) { cat ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {}
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(cat.second, fontSize = 28.sp)
                    }
                    Text(
                        text = cat.first,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Voir tout",
            fontSize = 12.sp,
            color = ElectricBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    GlassyCard(
        modifier = Modifier.width(165.dp),
        cornerRadius = 22.dp,
        shadowElevation = 3.dp,
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                PulseImage(
                    nameOrUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )

                // Badge Promotion / Stock
                if (product.badgeText != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(PromoRed, RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            product.badgeText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                // Favorite click overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(34.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .clickable { onFavoriteToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (product.isFavorite) PromoRed else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info text block
            Text(
                text = product.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${product.rating}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                Text(
                    text = "(${product.salesCount} ventes)",
                    fontSize = 9.sp,
                    color = TextSecondary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (product.oldPrice != null) {
                        Text(
                            text = "${product.oldPrice.toInt()} F",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                    Text(
                        text = "${product.price.toInt()} FCFA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricBlue
                    )
                }

                // Small circular buy button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(ElectricBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Buy",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BoutiqueCard(
    boutique: BoutiqueEntity,
    onClick: () -> Unit,
    onFollowToggle: () -> Unit
) {
    GlassyCard(
        modifier = Modifier.width(220.dp),
        cornerRadius = 22.dp,
        shadowElevation = 3.dp,
        onClick = onClick
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                ) {
                    PulseImage(
                        nameOrUrl = boutique.logoUrl,
                        contentDescription = boutique.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = boutique.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (boutique.isVerified) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = SuccessGreen,
                                modifier = Modifier
                                    .padding(start = 2.dp)
                                    .size(14.dp)
                                )
                        }
                    }
                    Text(
                        text = "Réponse: ~${boutique.responseTime}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = boutique.description,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⭐️ ${boutique.rating} (${boutique.salesCount} ventes)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (boutique.isFollowed) SuccessGreen.copy(alpha = 0.15f) else ElectricBlue)
                        .clickable { onFollowToggle() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (boutique.isFollowed) "Suivi" else "Suivre",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (boutique.isFollowed) SuccessGreen else Color.White
                    )
                }
            }
        }
    }
}
