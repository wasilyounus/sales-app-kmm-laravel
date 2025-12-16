package com.sales.app.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sales.app.util.isDesktop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    companyId: Int = 1,
    companies: List<com.sales.app.domain.model.Company> = emptyList(),
    onSelectCompany: (Int) -> Unit = {},
    onNavigateToLogin: () -> Unit,
    onNavigateToItems: () -> Unit,
    onNavigateToParties: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToPriceLists: () -> Unit,
    onNavigateToSync: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToTransfers: () -> Unit,
    onNavigateToCompanySettings: () -> Unit = {},
    onNavigateToDeliveryNotes: () -> Unit = {},
    onNavigateToGrns: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    viewModel: HomeViewModel
) {
    val stats by viewModel.stats.collectAsState()
    val company by viewModel.company.collectAsState()
    val modules by viewModel.modules.collectAsState()
    
    LaunchedEffect(companyId) {
        viewModel.loadStats(companyId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        // Wrapper box to anchor the dropdown to the title text
                        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                            var showCompanyMenu by remember { mutableStateOf(false) }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .clickable { showCompanyMenu = true }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = company?.name?.uppercase() ?: "SELECT COMPANY",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Company",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }

                            DropdownMenu(
                                expanded = showCompanyMenu,
                                onDismissRequest = { showCompanyMenu = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                companies.forEach { comp ->
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = comp.name,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            ) 
                                        },
                                        onClick = {
                                            onSelectCompany(comp.id)
                                            showCompanyMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.secondary
                ),
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onNavigateToLogin()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Grey/White
    ) { paddingValues ->
        if (isDesktop()) {
            DesktopHomeLayout(
                modifier = Modifier.padding(paddingValues),
                stats = stats,
                company = company,
                modules = modules,
                onNavigateToItems = onNavigateToItems,
                onNavigateToParties = onNavigateToParties,
                onNavigateToQuotes = onNavigateToQuotes,
                onNavigateToPayments = onNavigateToPayments,
                onNavigateToPriceLists = onNavigateToPriceLists,
                onNavigateToSync = onNavigateToSync,
                onNavigateToInventory = onNavigateToInventory,
                onNavigateToOrders = onNavigateToOrders,
                onNavigateToSales = onNavigateToSales,
                onNavigateToPurchases = onNavigateToPurchases,
                onNavigateToTransfers = onNavigateToTransfers,
                onNavigateToCompanySettings = onNavigateToCompanySettings,
                onNavigateToDeliveryNotes = onNavigateToDeliveryNotes,
                onNavigateToGrns = onNavigateToGrns
            )
        } else {
            MobileHomeLayout(
                modifier = Modifier.padding(paddingValues),
                stats = stats,
                company = company,
                modules = modules,
                onNavigateToItems = onNavigateToItems,
                onNavigateToParties = onNavigateToParties,
                onNavigateToQuotes = onNavigateToQuotes,
                onNavigateToPayments = onNavigateToPayments,
                onNavigateToPriceLists = onNavigateToPriceLists,
                onNavigateToSync = onNavigateToSync,
                onNavigateToInventory = onNavigateToInventory,
                onNavigateToOrders = onNavigateToOrders,
                onNavigateToSales = onNavigateToSales,
                onNavigateToPurchases = onNavigateToPurchases,
                onNavigateToTransfers = onNavigateToTransfers,
                onNavigateToCompanySettings = onNavigateToCompanySettings,
                onNavigateToDeliveryNotes = onNavigateToDeliveryNotes,
                onNavigateToGrns = onNavigateToGrns
            )
        }
    }
}

@Composable
private fun MobileHomeLayout(
    modifier: Modifier = Modifier,
    stats: HomeStats,
    company: com.sales.app.domain.model.Company?,
    modules: List<com.sales.app.domain.model.Module>,
    onNavigateToItems: () -> Unit,
    onNavigateToParties: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToPriceLists: () -> Unit,
    onNavigateToSync: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToTransfers: () -> Unit,
    onNavigateToCompanySettings: () -> Unit,
    onNavigateToDeliveryNotes: () -> Unit,
    onNavigateToGrns: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Total Revenue Card
        RevenueCard()
        
        // 2. Orders & Pending Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SmallStatCard(label = "ORDERS", value = stats.quotesCount.toString(), isHighlighted = false)
            }
            Box(modifier = Modifier.weight(1f)) {
                SmallStatCard(label = "PENDING", value = "12", isHighlighted = true)
            }
        }
        
        // 3. Graph Card
        SalesGraphCard()
        
        // 4. Functional Modules - DYNAMIC
        val noOp: () -> Unit = {}
        
        // Helper to convert Domain Module to UI ModuleData
        fun mapToModuleData(module: com.sales.app.domain.model.Module, onClick: () -> Unit): ModuleData {
            return ModuleData(
                title = module.name,
                subtitle = module.description ?: "",
                icon = getModuleIcon(module.icon),
                color = parseHexColor(module.bgColor),
                enabled = module.isEnabled,
                onClick = onClick
            )
        }
        
        // Inventory Management
        val inventoryModules = modules.filter { it.slug == "items" || it.slug == "inventory" || it.slug == "prices" }
            .map { module ->
                val onClick = when(module.slug) {
                    "items" -> onNavigateToItems
                    "inventory" -> onNavigateToInventory
                    "prices" -> onNavigateToPriceLists
                    else -> noOp
                }
                mapToModuleData(module, onClick)
            }
        
        if (inventoryModules.isNotEmpty()) {
            CategorySection(title = "Inventory Management", modules = inventoryModules)
        }
        
        // Customer Relationship
        val customerModules = modules.filter { it.slug == "parties" || it.slug == "payments" }
            .map { module ->
                val onClick = when(module.slug) {
                    "parties" -> onNavigateToParties
                    "payments" -> onNavigateToPayments
                    else -> noOp
                }
                mapToModuleData(module, onClick)
            }

        if (customerModules.isNotEmpty()) {
            CategorySection(title = "Customer Relationship", modules = customerModules)
        }
        
        // Transactions
        val transactionModules = modules.filter { 
               it.slug == "quotes" || 
               it.slug == "orders" || 
               it.slug == "sales" || 
               it.slug == "purchases" || 
               it.slug == "transfers" ||
               it.slug == "delivery-notes" ||
               it.slug == "grns"
            }
            .map { module ->
                val onClick = when(module.slug) {
                    "quotes" -> onNavigateToQuotes
                    "orders" -> onNavigateToOrders
                    "sales" -> onNavigateToSales
                    "purchases" -> onNavigateToPurchases
                    "transfers" -> onNavigateToTransfers
                    "delivery-notes" -> onNavigateToDeliveryNotes
                    "grns" -> onNavigateToGrns
                    else -> noOp
                }
                mapToModuleData(module, onClick)
            }.toMutableList()
            
            // Check visibility flags (although if they are enabled in DB, they show up, 
            // but we might want to respect company settings too for hiding/showing)
            // The DB 'isEnabled' handles general availability, but Company settings might hide specific features.
            // For now, mimicking original logic:
            val finalTransactionModules = transactionModules.filter { 
                when(it.title) { // Fallback to title matching since ID/Original slug lost in ModuleData
                     // Actually, we can just trust the list, but let's filter if company setting is explicit
                     "Delivery Notes" -> company?.enableDeliveryNotes != false
                     "GRNs" -> company?.enableGrns != false
                     else -> true
                }
            }

        if (finalTransactionModules.isNotEmpty()) {
            CategorySection(title = "Transactions", modules = finalTransactionModules)
        }
        
        // System
        // Combine DB modules + Hardcoded
        val syncModules = modules.filter { it.slug == "sync" }
             .map { module ->
                val onClick = when(module.slug) {
                    "sync" -> onNavigateToSync
                    else -> noOp
                }
                mapToModuleData(module, onClick)
            }
            
        val systemModules = syncModules + listOf(
             ModuleData("Company Settings", "Manage company settings", Icons.Outlined.Settings, Color(0xFF78909C), true, onNavigateToCompanySettings),
             ModuleData("Updates", "Check for updates", Icons.Outlined.Update, Color(0xFF90A4AE), false, noOp)
        )
        
        if (systemModules.isNotEmpty()) {
             CategorySection(title = "System", modules = systemModules)
        }
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
            modifier = Modifier.padding(bottom = 4.dp, top = 8.dp)
        )
        
        modules.forEach { module ->
            if (module.enabled) {
                ModuleRow(
                    icon = module.icon,
                    iconBg = module.color.copy(alpha = 0.1f),
                    iconColor = module.color,
                    title = module.title,
                    subtitle = module.subtitle,
                    onClick = module.onClick
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

data class ModuleData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val enabled: Boolean,
    val onClick: () -> Unit
)

@Composable
private fun DesktopHomeLayout(
    modifier: Modifier = Modifier,
    stats: HomeStats,
    company: com.sales.app.domain.model.Company?,
    modules: List<com.sales.app.domain.model.Module>,
    onNavigateToItems: () -> Unit,
    onNavigateToParties: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToPriceLists: () -> Unit,
    onNavigateToSync: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToSales: () -> Unit,
    onNavigateToPurchases: () -> Unit,
    onNavigateToTransfers: () -> Unit,
    onNavigateToCompanySettings: () -> Unit,
    onNavigateToDeliveryNotes: () -> Unit,
    onNavigateToGrns: () -> Unit
) {
    // Keep a simple grid for desktop for now, matching the new styling
    Row(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
             RevenueCard()
             Spacer(modifier = Modifier.height(16.dp))
             Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    SmallStatCard(label = "ORDERS", value = stats.quotesCount.toString(), isHighlighted = false)
                }
                Box(modifier = Modifier.weight(1f)) {
                    SmallStatCard(label = "PENDING", value = "12", isHighlighted = true)
                }
             }
             Spacer(modifier = Modifier.height(16.dp))
             SalesGraphCard()
        }
        
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Text("Quick Access", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reusing ModuleData Logic for Desktop
            val noOp: () -> Unit = {}
            fun mapToModuleData(module: com.sales.app.domain.model.Module, onClick: () -> Unit): ModuleData {
                return ModuleData(
                    title = module.name,
                    subtitle = module.description ?: "",
                    icon = getModuleIcon(module.icon),
                    color = parseHexColor(module.bgColor),
                    enabled = module.isEnabled,
                    onClick = onClick
                )
            }
            
            // Inventory Management
            val inventoryModules = modules.filter { it.slug == "items" || it.slug == "inventory" || it.slug == "prices" }
                .map { module ->
                    val onClick = when(module.slug) {
                        "items" -> onNavigateToItems
                        "inventory" -> onNavigateToInventory
                        "prices" -> onNavigateToPriceLists
                        else -> noOp
                    }
                    mapToModuleData(module, onClick)
                }
            
            if (inventoryModules.isNotEmpty()) {
                CategorySection(title = "Inventory Management", modules = inventoryModules)
            }
            
            // Customer Relationship
            val customerModules = modules.filter { it.slug == "parties" || it.slug == "payments" }
                .map { module ->
                    val onClick = when(module.slug) {
                        "parties" -> onNavigateToParties
                        "payments" -> onNavigateToPayments
                        else -> noOp
                    }
                    mapToModuleData(module, onClick)
                }

            if (customerModules.isNotEmpty()) {
                CategorySection(title = "Customer Relationship", modules = customerModules)
            }
            
            // Transactions
            val transactionModules = modules.filter { 
                   it.slug == "quotes" || 
                   it.slug == "orders" || 
                   it.slug == "sales" || 
                   it.slug == "purchases" || 
                   it.slug == "transfers" ||
                   it.slug == "delivery-notes" ||
                   it.slug == "grns"
                }
                .map { module ->
                    val onClick = when(module.slug) {
                        "quotes" -> onNavigateToQuotes
                        "orders" -> onNavigateToOrders
                        "sales" -> onNavigateToSales
                        "purchases" -> onNavigateToPurchases
                        "transfers" -> onNavigateToTransfers
                        "delivery-notes" -> onNavigateToDeliveryNotes
                        "grns" -> onNavigateToGrns
                        else -> noOp
                    }
                    mapToModuleData(module, onClick)
                }
            
             val finalTransactionModules = transactionModules.filter { 
                when(it.title) {
                     "Delivery Notes" -> company?.enableDeliveryNotes != false
                     "GRNs" -> company?.enableGrns != false
                     else -> true
                }
            }

            if (finalTransactionModules.isNotEmpty()) {
                CategorySection(title = "Transactions", modules = finalTransactionModules)
            }
            
            // System
            val syncModules = modules.filter { it.slug == "sync" }
                 .map { module ->
                    val onClick = when(module.slug) {
                        "sync" -> onNavigateToSync
                        else -> noOp
                    }
                    mapToModuleData(module, onClick)
                }
                
            val systemModules = syncModules + listOf(
                 ModuleData("Company Settings", "Manage company settings", Icons.Outlined.Settings, Color(0xFF78909C), true, onNavigateToCompanySettings),
                 ModuleData("Updates", "Check for updates", Icons.Outlined.Update, Color(0xFF90A4AE), false, noOp)
            )
            
            if (systemModules.isNotEmpty()) {
                 CategorySection(title = "System", modules = systemModules)
            }
        }
    }
}

// --- VTC Components ---

@Composable
fun RevenueCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat white card on grey bg look
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "TOTAL REVENUE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$154,000",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary // Navy (Light) or Gold (Dark)
            )
        }
    }
}

@Composable
fun SmallStatCard(label: String, value: String, isHighlighted: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isHighlighted) Color(0xFFEF6C00) else MaterialTheme.colorScheme.primary // Orange or Navy
            )
        }
    }
}

@Composable
fun SalesGraphCard() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    
    // Dynamic path color based on background
    // Light Mode: BG = Navy (Primary), Path = Gold (0xFFE0C068)
    // Dark Mode: BG = Gold (Primary), Path = Black (onPrimary)
    // Note: We check if primary is Gold (Dark Mode). If so, use Black/Navy for contrast.
    val isGoldBackground = primaryColor == Color(0xFFE0C068)
    val pathColor = if (isGoldBackground) onPrimaryColor else Color(0xFFE0C068)
    val gridColor = onPrimaryColor.copy(alpha = 0.2f)

    Card(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = primaryColor)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Draw a simple path graph
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                // Draw grid lines (subtle)
                drawLine(gridColor, Offset(0f, height * 0.25f), Offset(width, height * 0.25f))
                drawLine(gridColor, Offset(0f, height * 0.5f), Offset(width, height * 0.5f))
                drawLine(gridColor, Offset(0f, height * 0.75f), Offset(width, height * 0.75f))
                
                // Draw Path (Mock data)
                val path = Path().apply {
                    moveTo(0f, height * 0.8f)
                    cubicTo(width * 0.2f, height * 0.9f, width * 0.3f, height * 0.4f, width * 0.5f, height * 0.5f)
                    cubicTo(width * 0.7f, height * 0.6f, width * 0.8f, height * 0.2f, width, height * 0.3f)
                }
                
                drawPath(
                    path = path,
                    color = pathColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Draw gradient under path
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }
                
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            pathColor.copy(alpha = 0.3f),
                            pathColor.copy(alpha = 0.0f)
                        ),
                        startY = 0f,
                        endY = height
                    )
                )
            }
        }
    }
}

@Composable
fun ModuleRow(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
