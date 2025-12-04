package com.sales.app.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sales.app.util.isDesktop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    accountId: Int = 1,
    onNavigateToLogin: () -> Unit,
    onNavigateToItems: () -> Unit,
    onNavigateToParties: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToSync: () -> Unit,
    onNavigateToAccountSettings: () -> Unit = {},
    viewModel: HomeViewModel
) {
    val stats by viewModel.stats.collectAsState()
    
    LaunchedEffect(accountId) {
        viewModel.loadStats(accountId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Sales Dashboard",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onNavigateToLogin()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isDesktop()) {
            DesktopHomeLayout(
                modifier = Modifier.padding(paddingValues),
                stats = stats,
                onNavigateToItems = onNavigateToItems,
                onNavigateToParties = onNavigateToParties,
                onNavigateToQuotes = onNavigateToQuotes,
                onNavigateToSync = onNavigateToSync,
                onNavigateToAccountSettings = onNavigateToAccountSettings
            )
        } else {
            MobileHomeLayout(
                modifier = Modifier.padding(paddingValues),
                stats = stats,
                onNavigateToItems = onNavigateToItems,
                onNavigateToParties = onNavigateToParties,
                onNavigateToQuotes = onNavigateToQuotes,
                onNavigateToSync = onNavigateToSync,
                onNavigateToAccountSettings = onNavigateToAccountSettings
            )
        }
    }
}

@Composable
private fun DesktopHomeLayout(
    modifier: Modifier = Modifier,
    stats: HomeStats,
    onNavigateToItems: () -> Unit,
    onNavigateToParties: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToSync: () -> Unit,
    onNavigateToAccountSettings: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Left column - Welcome & Stats
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            WelcomeCard()
            QuickStatsCard(stats)
        }
        
        // Right column - Modules by Category
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Inventory Management
            CategorySection(
                title = "Inventory Management",
                modules = listOf(
                    ModuleData("Items", "Manage your product catalog", Icons.Default.Inventory, Color(0xFF6750A4), true, onNavigateToItems),
                    ModuleData("Inventory", "Track stock levels", Icons.Default.Warehouse, Color(0xFF8E6FCC), false) {},
                    ModuleData("Prices", "Manage pricing", Icons.Default.AttachMoney, Color(0xFFB39DDB), false) {}
                )
            )
            
            // Customer Relationship
            CategorySection(
                title = "Customer Relationship",
                modules = listOf(
                    ModuleData("Parties", "Manage customers and suppliers", Icons.Default.People, Color(0xFF006A6A), true, onNavigateToParties),
                    ModuleData("Payments", "Track payments", Icons.Default.Payment, Color(0xFF00897B), false) {}
                )
            )
            
            // Transactions
            CategorySection(
                title = "Transactions",
                modules = listOf(
                    ModuleData("Quotes", "Create and manage quotes", Icons.Default.RequestQuote, Color(0xFF8E4585), true, onNavigateToQuotes),
                    ModuleData("Orders", "Manage orders", Icons.Default.ShoppingCart, Color(0xFFAB47BC), false) {},
                    ModuleData("Sales", "Record sales", Icons.Default.TrendingUp, Color(0xFFC1689B), false) {},
                    ModuleData("Purchases", "Track purchases", Icons.Default.ShoppingBag, Color(0xFFAA5A98), false) {},
                    ModuleData("Transfers", "Stock transfers", Icons.Default.MoveDown, Color(0xFF9C4699), false) {}
                )
            )
            
            // System
            CategorySection(
                title = "System",
                modules = listOf(
                    ModuleData("Sync", "Synchronize with server", Icons.Default.Sync, Color(0xFF5F6368), true, onNavigateToSync),
                    ModuleData("Account Settings", "Manage account settings", Icons.Default.Settings, Color(0xFF78909C), true, onNavigateToAccountSettings),
                    ModuleData("Updates", "Check for updates", Icons.Default.Update, Color(0xFF90A4AE), false) {}
                )
            )
        }
    }
}

@Composable
private fun MobileHomeLayout(
    modifier: Modifier = Modifier,
    stats: HomeStats,
    onNavigateToItems: () -> Unit,
    onNavigateToParties: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToSync: () -> Unit,
    onNavigateToAccountSettings: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WelcomeCard()
        QuickStatsCard(stats)
        
        // Inventory Management
        CategorySection(
            title = "Inventory Management",
            modules = listOf(
                ModuleData("Items", "Manage your product catalog", Icons.Default.Inventory, Color(0xFF6750A4), true, onNavigateToItems),
                ModuleData("Inventory", "Track stock levels", Icons.Default.Warehouse, Color(0xFF8E6FCC), false) {},
                ModuleData("Prices", "Manage pricing", Icons.Default.AttachMoney, Color(0xFFB39DDB), false) {}
            )
        )
        
        // Customer Relationship
        CategorySection(
            title = "Customer Relationship",
            modules = listOf(
                ModuleData("Parties", "Manage customers and suppliers", Icons.Default.People, Color(0xFF006A6A), true, onNavigateToParties),
                ModuleData("Payments", "Track payments", Icons.Default.Payment, Color(0xFF00897B), false) {}
            )
        )
        
        // Transactions
        CategorySection(
            title = "Transactions",
            modules = listOf(
                ModuleData("Quotes", "Create and manage quotes", Icons.Default.RequestQuote, Color(0xFF8E4585), true, onNavigateToQuotes),
                ModuleData("Orders", "Manage orders", Icons.Default.ShoppingCart, Color(0xFFAB47BC), false) {},
                ModuleData("Sales", "Record sales", Icons.Default.TrendingUp, Color(0xFFC1689B), false) {},
                ModuleData("Purchases", "Track purchases", Icons.Default.ShoppingBag, Color(0xFFAA5A98), false) {},
                ModuleData("Transfers", "Stock transfers", Icons.Default.MoveDown, Color(0xFF9C4699), false) {}
            )
        )
        
        // System
        CategorySection(
            title = "System",
            modules = listOf(
                ModuleData("Sync", "Synchronize with server", Icons.Default.Sync, Color(0xFF5F6368), true, onNavigateToSync),
                ModuleData("Account Settings", "Manage account settings", Icons.Default.Settings, Color(0xFF78909C), true, onNavigateToAccountSettings),
                ModuleData("Updates", "Check for updates", Icons.Default.Update, Color(0xFF90A4AE), false) {}
            )
        )
    }
}

@Composable
private fun CategorySection(
    title: String,
    modules: List<ModuleData>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        modules.forEach { module ->
            FeatureCard(
                title = module.title,
                description = module.description,
                icon = module.icon,
                iconColor = module.iconColor,
                enabled = module.enabled,
                onClick = module.onClick
            )
        }
    }
}

data class ModuleData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val enabled: Boolean,
    val onClick: () -> Unit
)

@Composable
private fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Welcome Back!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Manage your sales operations efficiently",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun QuickStatsCard(stats: HomeStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                "Quick Stats",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Items",
                    value = stats.itemsCount.toString(),
                    icon = Icons.Default.Inventory,
                    color = Color(0xFF6750A4)
                )
                StatItem(
                    label = "Parties",
                    value = stats.partiesCount.toString(),
                    icon = Icons.Default.People,
                    color = Color(0xFF006A6A)
                )
                StatItem(
                    label = "Quotes",
                    value = stats.quotesCount.toString(),
                    icon = Icons.Default.RequestQuote,
                    color = Color(0xFF8E4585)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = if (enabled) onClick else {{}},
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (enabled) 2.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = iconColor.copy(alpha = if (enabled) 0.1f else 0.05f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor.copy(alpha = if (enabled) 1f else 0.4f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    if (!enabled) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                "Coming Soon",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (enabled) 1f else 0.6f
                    )
                )
            }
            
            if (enabled) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
