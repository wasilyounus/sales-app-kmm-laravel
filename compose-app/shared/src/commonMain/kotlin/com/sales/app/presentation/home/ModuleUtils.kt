package com.sales.app.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

fun getModuleIcon(name: String): ImageVector {
    return when (name) {
        // Inventory
        "Inventory" -> Icons.Outlined.Inventory2 // Box icon (Matches Web Items 'Package')
        "Warehouse" -> Icons.Outlined.Warehouse // Warehouse icon (Distinct from Items)
        "AttachMoney" -> Icons.Outlined.LocalOffer // Tag icon (Matches Web Prices 'Tags')
        
        // Customer
        "People" -> Icons.Outlined.People
        "Payment" -> Icons.Outlined.AccountBalanceWallet // Wallet icon (Matches Web Payments 'Wallet')
        
        // Transactions
        "RequestQuote" -> Icons.Outlined.Description // File/Doc icon (Matches Web Quotes 'FileText')
        "MoveDown" -> Icons.Outlined.MoveDown
        "LocalShipping" -> Icons.Outlined.LocalShipping
        
        // Orders
        "Orders", "ShoppingCart" -> Icons.AutoMirrored.Outlined.Assignment
        // Sales
        "Sales", "TrendingUp" -> Icons.Outlined.ShoppingCart
        // Purchases
        "Purchases", "ShoppingBag" -> Icons.Outlined.AllInbox
        // GRNs
        "GRN", "Inventory2" -> Icons.Outlined.Archive
        
        // System
        // Settings
        "Settings", "Settings" -> Icons.Outlined.Settings
        // Sync
        "Sync", "Sync" -> Icons.Outlined.Sync
        "Update" -> Icons.Outlined.Update
        
        // Default
        else -> Icons.AutoMirrored.Outlined.Help // Default fallback
    }
}

fun parseHexColor(hex: String): androidx.compose.ui.graphics.Color {
    return try {
        val cleanHex = hex.removePrefix("#")
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            androidx.compose.ui.graphics.Color(colorInt or 0xFF000000)
        } else {
            androidx.compose.ui.graphics.Color(colorInt)
        }
    } catch (e: Exception) {
        androidx.compose.ui.graphics.Color.Gray // Fallback
    }
}
