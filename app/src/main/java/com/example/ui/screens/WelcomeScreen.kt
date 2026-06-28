package com.example.ui.screens

import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassyCard
import com.example.ui.components.PulseButton
import com.example.ui.components.PulseImage
import com.example.ui.theme.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(onLoginSuccess: () -> Unit) {
    var showLoginSheet by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var otpRequested by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var isSendingOtp by remember { mutableStateOf(false) }
    var generatedOtp by remember { mutableStateOf("") }
    var secondsRemaining by remember { mutableStateOf(60) }
    var isGoogleLoggingIn by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Country code support
    data class Country(val name: String, val code: String, val flag: String)
    val countries = remember {
        listOf(
            Country("Burkina Faso", "+226", "🇧🇫"),
            Country("Côte d'Ivoire", "+225", "🇨🇮"),
            Country("Sénégal", "+221", "🇸🇳"),
            Country("Mali", "+223", "🇲🇱"),
            Country("Niger", "+227", "🇳🇪"),
            Country("Togo", "+228", "🇹🇬"),
            Country("Bénin", "+229", "🇧🇯"),
            Country("Cameroun", "+237", "🇨🇲"),
            Country("France", "+33", "🇫🇷")
        )
    }
    var selectedCountry by remember { mutableStateOf(countries[0]) } // Pre-select BF (+226)
    var expandedCountryDropdown by remember { mutableStateOf(false) }

    // Google Sign-In options
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isGoogleLoggingIn = false
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                Toast.makeText(context, "Bienvenue ${account.displayName} ! ✨", Toast.LENGTH_LONG).show()
                onLoginSuccess()
            } else {
                Toast.makeText(context, "Mode Démo : Connexion simulée avec succès ! 🌟", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Mode Démo : Authentification réussie ! 🎉", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }

    val vibrator = remember {
        try {
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } catch (e: Exception) {
            null
        }
    }

    fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(otpRequested) {
        if (otpRequested) {
            secondsRemaining = 60
            while (secondsRemaining > 0) {
                delay(1000L)
                secondsRemaining--
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        // Hero Background Illustration (Onboarding)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                PulseImage(
                    nameOrUrl = "img_onboarding_hero",
                    contentDescription = "Pulse Marketplace Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Bottom translucent sweep
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, LightBg)
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pulse Market",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlue,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "La marketplace intelligente nouvelle génération",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // Three Highlight Points
                HighlightItem(
                    title = "🛍 Acheter intelligemment",
                    desc = "Trouvez les meilleurs articles de luxe ou tech en tapant simplement une phrase."
                )
                HighlightItem(
                    title = "🚀 Vendre professionnellement",
                    desc = "Créez votre boutique en 2 min, améliorez vos descriptions automatiquement avec l'IA."
                )
                HighlightItem(
                    title = "🔥 Découvrir le Flux Social",
                    desc = "Suivez vos boutiques préférées, aimez les nouveautés et contactez-les via WhatsApp."
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Action buttons
                PulseButton(
                    text = "Continuer avec téléphone",
                    onClick = {
                        triggerVibration()
                        showLoginSheet = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = ElectricBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        triggerVibration()
                        isGoogleLoggingIn = true
                        try {
                            googleLauncher.launch(googleSignInClient.signInIntent)
                        } catch (e: Exception) {
                            isGoogleLoggingIn = false
                            Toast.makeText(context, "Mode Démo : Redirection de connexion...", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = BorderStroke(1.dp, TextSecondary.copy(alpha = 0.3f))
                ) {
                    if (isGoogleLoggingIn) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = ElectricBlue, strokeWidth = 2.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color.Transparent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("G", fontWeight = FontWeight.Black, color = PromoRed)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Continuer avec Google", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Login Bottom Sheet Overlay
        AnimatedVisibility(
            visible = showLoginSheet,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { showLoginSheet = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                GlassyCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) {}, // Prevent clicks through
                    cornerRadius = 28.dp,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Drag handle
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(5.dp)
                                .background(TextSecondary.copy(alpha = 0.3f), CircleShape)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (!otpRequested) {
                            Text(
                                text = "Connexion Sécurisée",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Entrez votre numéro de téléphone pour recevoir un code OTP",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                leadingIcon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clickable { expandedCountryDropdown = true }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(text = selectedCountry.flag, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = selectedCountry.code, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        DropdownMenu(
                                            expanded = expandedCountryDropdown,
                                            onDismissRequest = { expandedCountryDropdown = false }
                                        ) {
                                            countries.forEach { country ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(country.flag, fontSize = 18.sp)
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Text("${country.name} (${country.code})", color = TextPrimary, fontSize = 14.sp)
                                                        }
                                                    },
                                                    onClick = {
                                                        selectedCountry = country
                                                        expandedCountryDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                },
                                placeholder = { Text("Numéro de téléphone") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    focusedLabelColor = ElectricBlue
                                )
                            )

                            if (isSendingOtp) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = ElectricBlue)
                                }
                            } else {
                                PulseButton(
                                    text = "Obtenir le code",
                                    onClick = {
                                        if (phoneNumber.length >= 8) {
                                            triggerVibration()
                                            isSendingOtp = true
                                            scope.launch {
                                                delay(1500)
                                                generatedOtp = (1000..9999).random().toString()
                                                isSendingOtp = false
                                                otpRequested = true
                                                Toast.makeText(context, "Code OTP envoyé : $generatedOtp", Toast.LENGTH_LONG).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Veuillez entrer un numéro valide (min. 8 chiffres)", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Text(
                                text = "Vérification OTP",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Saisissez le code à 4 chiffres envoyé au ${selectedCountry.code} $phoneNumber",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            
                            // Visual hint to assist sandbox testing
                            Text(
                                text = "🔑 Code d'accès de test : $generatedOtp",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // 4 Box OTP Visualizer
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                repeat(4) { index ->
                                    val digit = otpCode.getOrNull(index)?.toString() ?: ""
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .border(
                                                width = 2.dp,
                                                color = if (otpCode.length == index) ElectricBlue else TextSecondary.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .background(LightCard, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = digit,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }

                            // Simulated Custom Premium Keypad for Vibration
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val keys = listOf(
                                    listOf("1", "2", "3"),
                                    listOf("4", "5", "6"),
                                    listOf("7", "8", "9"),
                                    listOf("Clear", "0", "OK")
                                )

                                for (row in keys) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        for (key in row) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(54.dp)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(TextSecondary.copy(alpha = 0.08f))
                                                    .clickable {
                                                        triggerVibration()
                                                        when (key) {
                                                            "Clear" -> {
                                                                if (otpCode.isNotEmpty()) otpCode = otpCode.dropLast(1)
                                                            }
                                                            "OK" -> {
                                                                if (otpCode == generatedOtp) {
                                                                    Toast.makeText(context, "Connexion réussie ! 🎉", Toast.LENGTH_SHORT).show()
                                                                    onLoginSuccess()
                                                                } else {
                                                                    Toast.makeText(context, "Code incorrect ! Réessayez.", Toast.LENGTH_SHORT).show()
                                                                    otpCode = ""
                                                                }
                                                            }
                                                            else -> {
                                                                if (otpCode.length < 4) {
                                                                    otpCode += key
                                                                    if (otpCode.length == 4) {
                                                                        if (otpCode == generatedOtp) {
                                                                            Toast.makeText(context, "Connexion réussie ! 🎉", Toast.LENGTH_SHORT).show()
                                                                            onLoginSuccess()
                                                                        } else {
                                                                            Toast.makeText(context, "Code incorrect ! Réessayez.", Toast.LENGTH_SHORT).show()
                                                                            otpCode = ""
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = key,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (secondsRemaining > 0) "Renvoyer le code dans ${secondsRemaining}s" else "Renvoyer le code",
                                fontSize = 14.sp,
                                color = if (secondsRemaining > 0) TextSecondary else PremiumViolet,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable(enabled = secondsRemaining == 0) {
                                        triggerVibration()
                                        otpCode = ""
                                        isSendingOtp = true
                                        scope.launch {
                                            delay(1500)
                                            generatedOtp = (1000..9999).random().toString()
                                            isSendingOtp = false
                                            otpRequested = true
                                            secondsRemaining = 60
                                            Toast.makeText(context, "Nouveau code OTP envoyé : $generatedOtp", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    .padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightItem(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = desc,
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
