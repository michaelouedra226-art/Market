package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.ui.ProductDraft
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PulseViewModel
import com.example.ui.components.GlassyCard
import com.example.ui.components.PulseButton
import com.example.ui.theme.*

@Composable
fun PublishScreen(viewModel: PulseViewModel) {
    val step by viewModel.publishStep.collectAsState()
    val draft by viewModel.productDraft.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Offset for bottom nav
        ) {
            // Header with Steps Progress indicator
            PublishHeader(currentStep = step)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (step) {
                    1 -> Step1Media(draft = draft, onImageSelected = { viewModel.updateDraftImage(it) })
                    2 -> Step2Info(
                        draft = draft,
                        onTitleChange = { viewModel.updateDraftTitle(it) },
                        onDescChange = { viewModel.updateDraftDescription(it) },
                        onCategoryChange = { viewModel.updateDraftCategory(it) }
                    )
                    3 -> Step3Pricing(
                        draft = draft,
                        onPriceChange = { viewModel.updateDraftPrice(it) },
                        onOldPriceChange = { viewModel.updateDraftOldPrice(it) },
                        onStockChange = { viewModel.updateDraftStock(it) },
                        onFlashToggle = { viewModel.updateDraftFlash(it) }
                    )
                    4 -> Step4AIOptimizer(
                        draft = draft,
                        onOptimizeClick = { viewModel.optimizeProductWithAI() },
                        onPublishClick = { viewModel.commitPublishDraft() }
                    )
                }
            }

            // Bottom Navigation Row for Steps
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { viewModel.setPublishStep(step - 1) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Précédent")
                    }
                } else {
                    Spacer(modifier = Modifier.width(10.dp))
                }

                if (step < 4) {
                    PulseButton(
                        text = "Suivant",
                        onClick = { viewModel.setPublishStep(step + 1) },
                        modifier = Modifier.width(120.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PublishHeader(currentStep: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ElectricBlue.copy(alpha = 0.05f))
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        Column {
            Text(
                "Publier un Produit",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Step timeline bubbles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { i ->
                    val stepNum = i + 1
                    val isDone = stepNum < currentStep
                    val isActive = stepNum == currentStep

                    val bubbleColor = when {
                        isActive -> PremiumViolet
                        isDone -> SuccessGreen
                        else -> TextSecondary.copy(alpha = 0.3f)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(bubbleColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Text("$stepNum", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        if (stepNum < 4) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .width(40.dp)
                                    .height(2.dp)
                                    .background(if (isDone) SuccessGreen else TextSecondary.copy(alpha = 0.2f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Step1Media(draft: ProductDraft, onImageSelected: (String) -> Unit) {
    Column {
        Text("Étape 1 : Médias du produit", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Ajoutez de superbes photos pour attirer vos clients. L'illustration par défaut de Pulse sera utilisée.", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(16.dp))

        // Large photo upload area mockup
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = TextSecondary.copy(alpha = 0.05f)),
            border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Prendre une photo / vidéo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Text("Supporte JPG, PNG, MP4", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Sélectionner une illustration premium pré-générée :", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        // Choice chips for premium generated images
        val choices = listOf(
            Pair("Logo Pulsant (Splash)", "img_splash_logo"),
            Pair("Illustration Sneakers (Onboarding)", "img_onboarding_hero"),
            Pair("Shop Étagères de Luxe (Boutique)", "img_boutique_banner")
        )

        for (choice in choices) {
            val isSelected = draft.imageUrl == choice.second
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) ElectricBlue.copy(alpha = 0.1f) else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) ElectricBlue else TextSecondary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onImageSelected(choice.second) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = isSelected, onClick = { onImageSelected(choice.second) }, colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue))
                Spacer(modifier = Modifier.width(8.dp))
                Text(choice.first, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun Step2Info(
    draft: ProductDraft,
    onTitleChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Étape 2 : Informations Générales", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        OutlinedTextField(
            value = draft.title,
            onValueChange = onTitleChange,
            label = { Text("Nom du produit *") },
            placeholder = { Text("Ex: Montre élégante quartz") },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue)
        )

        OutlinedTextField(
            value = draft.description,
            onValueChange = onDescChange,
            label = { Text("Description détaillée *") },
            placeholder = { Text("Décrivez les caractéristiques du produit...") },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue)
        )

        // Dropdown Categories mockup with simple Row selector
        Text("Catégorie *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val cats = listOf("Horlogerie", "Mode", "High-Tech")
            for (c in cats) {
                val isSel = draft.category == c
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) ElectricBlue else TextSecondary.copy(alpha = 0.08f))
                        .clickable { onCategoryChange(c) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(c, color = if (isSel) Color.White else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Step3Pricing(
    draft: ProductDraft,
    onPriceChange: (Double) -> Unit,
    onOldPriceChange: (Double?) -> Unit,
    onStockChange: (Int) -> Unit,
    onFlashToggle: (Boolean) -> Unit
) {
    var priceText by remember { mutableStateOf(if (draft.price > 0.0) draft.price.toInt().toString() else "") }
    var oldPriceText by remember { mutableStateOf(draft.oldPrice?.toInt()?.toString() ?: "") }
    var stockText by remember { mutableStateOf(draft.stockCount.toString()) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Étape 3 : Tarification & Logistique", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        OutlinedTextField(
            value = priceText,
            onValueChange = {
                priceText = it
                onPriceChange(it.toDoubleOrNull() ?: 0.0)
            },
            label = { Text("Prix de vente (FCFA) *") },
            placeholder = { Text("Ex: 15000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue)
        )

        OutlinedTextField(
            value = oldPriceText,
            onValueChange = {
                oldPriceText = it
                onOldPriceChange(it.toDoubleOrNull())
            },
            label = { Text("Ancien prix barré (FCFA) - Optionnel") },
            placeholder = { Text("Ex: 20000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue)
        )

        OutlinedTextField(
            value = stockText,
            onValueChange = {
                stockText = it
                onStockChange(it.toIntOrNull() ?: 1)
            },
            label = { Text("Stock disponible *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = draft.isFlashSale, onCheckedChange = onFlashToggle, colors = CheckboxDefaults.colors(checkedColor = PromoRed))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("Activer la Vente Flash ⚡", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Text("Le produit sera affiché dans la section promo d'accueil.", fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun Step4AIOptimizer(
    draft: ProductDraft,
    onOptimizeClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Étape 4 : Assistant IA Copywriter", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Améliorez l'impact de votre fiche produit en 1 clic grâce à l'IA de Pulse Market.", fontSize = 12.sp, color = TextSecondary)

        // Optimize Button
        Button(
            onClick = onOptimizeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PremiumViolet)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (draft.isOptimizing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Optimiser ma fiche produit", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Preview Optimization outputs
        if (draft.optimizedDescription.isNotEmpty()) {
            Text("Aperçu de l'optimisation IA", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PremiumViolet)

            GlassyCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 18.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Titre Amélioré :", fontWeight = FontWeight.Bold, color = PremiumViolet, fontSize = 13.sp)
                    Text(draft.optimizedTitle, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)

                    Divider(color = TextSecondary.copy(alpha = 0.1f))

                    Text("Description Enrichie :", fontWeight = FontWeight.Bold, color = PremiumViolet, fontSize = 13.sp)
                    Text(draft.optimizedDescription, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp)

                    Divider(color = TextSecondary.copy(alpha = 0.1f))

                    Text("Tags SEO suggérés :", fontWeight = FontWeight.Bold, color = PremiumViolet, fontSize = 13.sp)
                    Text(draft.optimizedTags, fontSize = 12.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)

                    Divider(color = TextSecondary.copy(alpha = 0.1f))

                    Text("Conseil Prix Intelligent :", fontWeight = FontWeight.Bold, color = PremiumViolet, fontSize = 13.sp)
                    Text(draft.optimizedPriceAdvice, fontSize = 12.sp, color = TextSecondary)
                }
            }
        } else {
            // Static draft review before publish
            GlassyCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 18.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Fiche de base", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 12.sp)
                    Text(draft.title.ifEmpty { "[Pas de titre]" }, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Text("${draft.price.toInt()} FCFA", fontWeight = FontWeight.Black, color = ElectricBlue, fontSize = 15.sp)
                    Text(draft.description.ifEmpty { "[Pas de description]" }, fontSize = 12.sp, color = TextSecondary, maxLines = 3)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PulseButton(
            text = "Confirmer & Mettre en Ligne 🎉",
            onClick = onPublishClick,
            modifier = Modifier.fillMaxWidth(),
            containerColor = SuccessGreen
        )
    }
}
