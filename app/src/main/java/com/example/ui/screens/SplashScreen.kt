package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PulseImage
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.PremiumViolet
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1.05f else 0.85f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 80f),
        label = "scale"
    )

    // Launch the splash timer
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Cosmic Slate Dark
                        Color(0xFF030712)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alphaAnim).scale(scaleAnim)
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                PulseImage(
                    nameOrUrl = "img_splash_logo",
                    contentDescription = "Pulse Market Logo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PULSE MARKET",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "L'achat-vente intelligent & premium",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
        }
    }
}
