package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.*

@Composable
fun getDrawableId(name: String): Int {
    return when (name) {
        "img_splash_logo" -> R.drawable.img_splash_logo
        "img_onboarding_hero" -> R.drawable.img_onboarding_hero
        "img_boutique_banner" -> R.drawable.img_boutique_banner
        else -> R.drawable.img_splash_logo
    }
}

@Composable
fun PulseImage(
    nameOrUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (nameOrUrl.startsWith("img_")) {
        // Mock resource
        val resId = getDrawableId(nameOrUrl)
        androidx.compose.foundation.Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        // Load with Coil AsyncImage
        AsyncImage(
            model = nameOrUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
            error = painterResource(id = R.drawable.img_splash_logo),
            placeholder = painterResource(id = R.drawable.img_splash_logo)
        )
    }
}

@Composable
fun GlassyCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f),
        label = "click_scale"
    )

    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

    var cardModifier = modifier
        .scale(scale)
        .shadow(shadowElevation, RoundedCornerShape(cornerRadius), clip = false)
        .clip(RoundedCornerShape(cornerRadius))
        .background(surfaceColor)
        .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))

    if (onClick != null) {
        cardModifier = cardModifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = onClick
        )
    }

    Column(
        modifier = cardModifier.padding(16.dp),
        content = content
    )
}

@Composable
fun PulseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = ElectricBlue,
    contentColor: Color = Color.White,
    icon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "btn_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .height(54.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 1.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
        }
    }
}

@Composable
fun GradientIcon(
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        icon()
    }
}
