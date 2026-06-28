package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.data.database.AppDatabase
import com.example.data.repository.PulseRepository
import com.example.ui.PulseTab
import com.example.ui.PulseViewModel
import com.example.ui.PulseViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SQLite Room Database & Repository
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = PulseRepository(db)
        val factory = PulseViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, factory)[PulseViewModel::class.java]

        setContent {
            PulseMarketTheme {
                var isSplashFinished by remember { mutableStateOf(false) }
                var isLoggedIn by remember { mutableStateOf(false) }

                when {
                    !isSplashFinished -> {
                        SplashScreen(onSplashFinished = { isSplashFinished = true })
                    }
                    !isLoggedIn -> {
                        WelcomeScreen(onLoginSuccess = { isLoggedIn = true })
                    }
                    else -> {
                        PulseAppMainContent(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun PulseAppMainContent(viewModel: PulseViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val selectedBoutique by viewModel.selectedBoutique.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Only show bottom bar when not inside full screen details to maximize screen real-estate
            if (selectedProduct == null && selectedBoutique == null) {
                PulseBottomNavigationBar(
                    selectedTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Master Navigation router
            when {
                selectedProduct != null || selectedBoutique != null -> {
                    DetailScreen(viewModel = viewModel)
                }
                else -> {
                    when (currentTab) {
                        PulseTab.ACCUEIL -> HomeScreen(
                            viewModel = viewModel,
                            onProductSelected = { viewModel.selectProduct(it) },
                            onBoutiqueSelected = { viewModel.selectBoutique(it) }
                        )
                        PulseTab.EXPLORER -> ExplorerScreen(
                            viewModel = viewModel,
                            onProductSelected = { viewModel.selectProduct(it) }
                        )
                        PulseTab.PUBLIER -> PublishScreen(
                            viewModel = viewModel
                        )
                        PulseTab.FAVORIS -> FavoritesScreen(
                            viewModel = viewModel,
                            onProductSelected = { viewModel.selectProduct(it) },
                            onBoutiqueSelected = { viewModel.selectBoutique(it) }
                        )
                        PulseTab.PROFIL -> ProfileScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PulseBottomNavigationBar(
    selectedTab: PulseTab,
    onTabSelected: (PulseTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(72.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(PulseTab.ACCUEIL, "Accueil", Icons.Default.Home),
            Triple(PulseTab.EXPLORER, "Explorer", Icons.Default.Search),
            Triple(PulseTab.PUBLIER, "Publier", Icons.Default.AddCircle),
            Triple(PulseTab.FAVORIS, "Favoris", Icons.Default.Favorite),
            Triple(PulseTab.PROFIL, "Profil", Icons.Default.Person)
        )

        for (item in navItems) {
            val isSelected = selectedTab == item.first
            val iconColor = if (isSelected) ElectricBlue else TextSecondary

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.first) },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.third,
                            contentDescription = item.second,
                            tint = iconColor,
                            modifier = Modifier.size(if (isSelected) 26.dp else 22.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.second,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = iconColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = ElectricBlue.copy(alpha = 0.08f)
                )
            )
        }
    }
}
