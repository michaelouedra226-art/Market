package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.text.style.TextAlign
import com.example.ui.components.PulseImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.BoutiqueEntity
import com.example.data.database.ProductEntity
import com.example.ui.PulseViewModel
import com.example.ui.theme.*

@Composable
fun FavoritesScreen(
    viewModel: PulseViewModel,
    onProductSelected: (ProductEntity) -> Unit,
    onBoutiqueSelected: (BoutiqueEntity) -> Unit
) {
    val favProducts by viewModel.favoriteProducts.collectAsState()
    val boutiques by viewModel.boutiques.collectAsState()
    val followedBoutiques = boutiques.filter { it.isFollowed }

    var selectedSection by remember { mutableStateOf("Produits") } // "Produits" or "Boutiques"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Nav Offset
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    "Mes Favoris",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "Retrouvez vos articles coup de cœur et boutiques suivies",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Toggle tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val sections = listOf("Produits", "Boutiques")
                    for (sec in sections) {
                        val isSel = selectedSection == sec
                        val bgCol = if (isSel) ElectricBlue else TextSecondary.copy(alpha = 0.08f)
                        val textCol = if (isSel) Color.White else TextPrimary

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(bgCol, RoundedCornerShape(14.dp))
                                .clickable { selectedSection = sec }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(sec, color = textCol, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // List area
            if (selectedSection == "Produits") {
                if (favProducts.isEmpty()) {
                    EmptyState(title = "Aucun produit favori", desc = "Appuyez sur l'icône de cœur sur les fiches produits pour les retrouver ici.", icon = Icons.Default.Favorite)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(favProducts) { product ->
                            SearchProductRow(product = product, onClick = { onProductSelected(product) })
                        }
                    }
                }
            } else {
                if (followedBoutiques.isEmpty()) {
                    EmptyState(title = "Aucune boutique suivie", desc = "Suivez vos boutiques préférées pour recevoir des notifications d'offres exceptionnelles.", icon = Icons.Default.Home)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(followedBoutiques) { boutique ->
                            BoutiqueRow(
                                boutique = boutique,
                                onClick = { onBoutiqueSelected(boutique) },
                                onUnfollow = { viewModel.toggleFollowBoutique(boutique) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary.copy(alpha = 0.3f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                desc,
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun BoutiqueRow(
    boutique: BoutiqueEntity,
    onClick: () -> Unit,
    onUnfollow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(54.dp)) {
                PulseImage(
                    nameOrUrl = boutique.logoUrl,
                    contentDescription = boutique.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(boutique.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                Text("⭐ ${boutique.rating} (${boutique.salesCount} ventes)", fontSize = 11.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(onClick = onUnfollow) {
                Icon(Icons.Default.Favorite, contentDescription = "Unfollow", tint = PromoRed)
            }
        }
    }
}
