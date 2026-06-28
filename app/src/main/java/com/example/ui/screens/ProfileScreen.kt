package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PulseViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.PulseImage
import com.example.ui.theme.*

@Composable
fun ProfileScreen(viewModel: PulseViewModel) {
    val isSellerMode by viewModel.isSellerMode.collectAsState()

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
            // Header Toggle: Client vs Professional Seller
            ProfileHeaderToggle(
                isSeller = isSellerMode,
                onToggle = { viewModel.setSellerMode(it) }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isSellerMode) {
                    ClientProfileView()
                } else {
                    VendorDashboardView()
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderToggle(
    isSeller: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ElectricBlue.copy(alpha = 0.12f), Color.Transparent)
                )
            )
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isSeller) "Espace Vendeur" else "Mon Profil Pulse",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isSeller) "Tableau de Bord Professionnel" else "Membre Privilégié",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                // Avatar mockup
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                ) {
                    PulseImage(nameOrUrl = "img_splash_logo", contentDescription = null, modifier = Modifier.fillMaxSize())
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dual Switch button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TextSecondary.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isSeller) ElectricBlue else Color.Transparent)
                        .clickable { onToggle(false) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = if (!isSeller) Color.White else TextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Acheteur", color = if (!isSeller) Color.White else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSeller) PremiumViolet else Color.Transparent)
                        .clickable { onToggle(true) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = if (isSeller) Color.White else TextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Vendeur Pro", color = if (isSeller) Color.White else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ClientProfileView() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Loyalty Card in Glassmorphism style
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(ElectricBlue, PremiumViolet)
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Pulse Club", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Moussa Traoré", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("VIP Platine", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text("Solde de Points", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        Text("1 450 Points", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    }
                    Text("ID: #PM-88229", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                }
            }
        }

        // Action grid list
        Text("Mes Outils Client", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileMenuCard(icon = Icons.Default.ShoppingCart, title = "Mes Commandes", desc = "5 articles commandés", modifier = Modifier.weight(1f))
            ProfileMenuCard(icon = Icons.Default.Star, title = "Coupons de Remise", desc = "2 réductions dispo", modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileMenuCard(icon = Icons.Default.LocationOn, title = "Mes Adresses", desc = "Dakar Plateau, Sénégal", modifier = Modifier.weight(1f))
            ProfileMenuCard(icon = Icons.Default.List, title = "Moyen Paiement", desc = "Orange Money, Visa", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("Historique Récent", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

        // Simulating some order history items
        OrderHistoryRow(title = "Montre Quartz Chrono", price = "12 500 F", date = "Hier à 14:22", status = "En cours de livraison")
        OrderHistoryRow(title = "Sneakers Pulse Sport Pro", price = "24 500 F", date = "24 Juin 2026", status = "Livré ✅")
    }
}

@Composable
fun ProfileMenuCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable {},
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(ElectricBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
            Text(desc, fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun OrderHistoryRow(
    title: String,
    price: String,
    date: String,
    status: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Text(date, fontSize = 11.sp, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ElectricBlue)
                Text(status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (status.contains("cours")) PremiumViolet else SuccessGreen)
            }
        }
    }
}

@Composable
fun VendorDashboardView() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Basic Stats grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatHeaderCard(title = "Visiteurs", value = "1,240", desc = "Aujourd'hui", color = PremiumViolet, modifier = Modifier.weight(1f))
            StatHeaderCard(title = "Clics WhatsApp", value = "185", desc = "Intention d'achat", color = SuccessGreen, modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatHeaderCard(title = "Commandes", value = "34", desc = "8 en attente", color = ElectricBlue, modifier = Modifier.weight(1f))
            StatHeaderCard(title = "Revenus (est.)", value = "425k F", desc = "+12% cette sem.", color = PromoRed, modifier = Modifier.weight(1f))
        }

        // Custom Analytics Graph drawn with Canvas
        Text("Revenus Estimés (7 derniers jours)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Line Graph drawn on Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    val points = listOf(
                        Offset(0f, height * 0.8f),
                        Offset(width * 0.16f, height * 0.7f),
                        Offset(width * 0.33f, height * 0.45f),
                        Offset(width * 0.5f, height * 0.55f),
                        Offset(width * 0.66f, height * 0.25f),
                        Offset(width * 0.83f, height * 0.3f),
                        Offset(width, height * 0.1f)
                    )

                    // Draw grid lines
                    for (i in 1..3) {
                        val gridY = height * (i / 4f)
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.2f),
                            start = Offset(0f, gridY),
                            end = Offset(width, gridY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw path connection
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = PremiumViolet,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw gradient fill below the path
                    val fillPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(PremiumViolet.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )

                    // Draw circles on point nodes
                    for (point in points) {
                        drawCircle(
                            color = PremiumViolet,
                            radius = 5.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = point
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Warning alerts / stock alerts
        Text("Alertes Logistiques", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PromoRed.copy(alpha = 0.06f)),
            border = BorderStroke(1.dp, PromoRed.copy(alpha = 0.15f))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = PromoRed)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Alerte Stock Faible", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PromoRed)
                    Text("Le produit 'Sneakers Pulse Sport Pro' ne dispose plus que de 8 pièces en stock.", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun StatHeaderCard(
    title: String,
    value: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color, CircleShape)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(desc, fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
    }
}
