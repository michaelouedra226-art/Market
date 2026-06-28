package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.example.ui.components.PulseImage
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ProductEntity
import com.example.ui.PulseViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.PulseButton
import com.example.ui.theme.*

@Composable
fun ExplorerScreen(
    viewModel: PulseViewModel,
    onProductSelected: (ProductEntity) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var activeCategoryFilter by remember { mutableStateOf("Tous") }
    var activePriceFilter by remember { mutableStateOf("Tous les Prix") }
    var showAiAssistant by remember { mutableStateOf(false) }

    var typedQuery by remember { mutableStateOf("") }

    val categories = listOf("Tous", "Horlogerie", "Mode", "High-Tech")
    val priceFilters = listOf("Tous les Prix", "< 15000 FCFA", "15000 - 50000 FCFA", "> 50000 FCFA")

    // Filter products locally
    val filteredProducts = products.filter { product ->
        val matchesCategory = activeCategoryFilter == "Tous" || product.category.lowercase() == activeCategoryFilter.lowercase()
        val matchesPrice = when (activePriceFilter) {
            "< 15000 FCFA" -> product.price < 15000.0
            "15000 - 50000 FCFA" -> product.price in 15000.0..50000.0
            "> 50000 FCFA" -> product.price > 50000.0
            else -> true
        }
        val matchesSearch = typedQuery.isEmpty() || product.name.lowercase().contains(typedQuery.lowercase()) || product.description.lowercase().contains(typedQuery.lowercase())

        matchesCategory && matchesPrice && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Nav bar offset
        ) {
            // Header Search Input Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(ElectricBlue.copy(alpha = 0.15f), Color.Transparent)
                        )
                    )
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 12.dp)
            ) {
                Column {
                    Text(
                        text = "Recherche Intelligente",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = typedQuery,
                            onValueChange = { typedQuery = it },
                            placeholder = { Text("Écrivez votre besoin...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ElectricBlue) },
                            trailingIcon = {
                                if (typedQuery.isNotEmpty()) {
                                    IconButton(onClick = { typedQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f)
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                if (typedQuery.isNotEmpty()) {
                                    viewModel.triggerAiSearch(typedQuery)
                                    showAiAssistant = true
                                }
                            })
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Mic Button
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(PremiumViolet.copy(alpha = 0.12f))
                                .clickable {
                                    typedQuery = "Je cherche une montre élégante à moins de 15000 FCFA"
                                    viewModel.triggerAiSearch(typedQuery)
                                    showAiAssistant = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Voice", tint = PremiumViolet)
                        }
                    }
                }
            }

            // Quick AI Helper Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PremiumViolet.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, PremiumViolet.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PremiumViolet),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Assistant IA Pulse",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PremiumViolet
                        )
                        Text(
                            text = "Cherchez en langage naturel (ex: 'un cadeau de montre à -30%')",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Button(
                        onClick = {
                            showAiAssistant = true
                            if (typedQuery.isEmpty()) {
                                typedQuery = "Quelles sont les meilleures offres en promotion ?"
                            }
                            viewModel.triggerAiSearch(typedQuery)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PremiumViolet),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Activer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Category filters Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = activeCategoryFilter == cat,
                        onClick = { activeCategoryFilter = cat },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Price filters Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp)
            ) {
                items(priceFilters) { filter ->
                    FilterChip(
                        selected = activePriceFilter == filter,
                        onClick = { activePriceFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PremiumViolet,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Products list
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                        Text("Aucun produit ne correspond à ces critères.", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProducts) { product ->
                        SearchProductRow(product = product, onClick = { onProductSelected(product) })
                    }
                }
            }
        }

        // Floating AI Assistant Panel overlay
        AnimatedVisibility(
            visible = showAiAssistant,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showAiAssistant = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                GlassyCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(550.dp)
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
                                        .background(PremiumViolet, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Pulse Assistant IA", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            IconButton(onClick = { showAiAssistant = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = TextSecondary.copy(alpha = 0.15f))

                        // Conversational Chat Screen
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // User Message Box
                            if (searchQuery.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp))
                                            .background(ElectricBlue)
                                            .padding(12.dp)
                                    ) {
                                        Text(searchQuery, color = Color.White, fontSize = 14.sp)
                                    }
                                }
                            }

                            // AI Assistant Message Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp))
                                    .background(TextSecondary.copy(alpha = 0.05f))
                                    .border(1.dp, TextSecondary.copy(alpha = 0.1f), RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp))
                                    .padding(14.dp)
                            ) {
                                if (isAiLoading) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(color = PremiumViolet, strokeWidth = 3.dp, modifier = Modifier.size(30.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Recherche intelligente en cours...", color = TextSecondary, fontSize = 12.sp)
                                    }
                                } else {
                                    Text(
                                        text = aiResponse ?: "Entrez votre question dans la barre ci-dessous pour lancer l'analyse intelligente.",
                                        fontSize = 14.sp,
                                        color = TextPrimary,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }

                        // Bottom Text Input for Chatting with AI
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = typedQuery,
                                onValueChange = { typedQuery = it },
                                placeholder = { Text("Posez votre question...") },
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PremiumViolet),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(onSend = {
                                    if (typedQuery.isNotEmpty()) {
                                        viewModel.triggerAiSearch(typedQuery)
                                    }
                                })
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PremiumViolet)
                                    .clickable {
                                        if (typedQuery.isNotEmpty()) {
                                            viewModel.triggerAiSearch(typedQuery)
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
    }
}

@Composable
fun SearchProductRow(
    product: ProductEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                PulseImage(
                    nameOrUrl = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = product.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(ElectricBlue.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(product.category, fontSize = 10.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⭐️ ${product.rating}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "${product.price.toInt()} F",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = ElectricBlue
            )
        }
    }
}
