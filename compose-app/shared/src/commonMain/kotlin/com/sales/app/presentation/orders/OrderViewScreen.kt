package com.sales.app.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Share
import com.sales.app.presentation.common.PrintItem
import com.sales.app.presentation.common.TransactionPrintPreview
import com.sales.app.util.PrintData
import com.sales.app.util.PrintItem as UtilPrintItem
import com.sales.app.util.getPlatformShare

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderViewScreen(
    accountId: Int,
    orderId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: OrderViewViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var isPrintPreview by remember { mutableStateOf(false) }
    
    LaunchedEffect(accountId, orderId) {
        viewModel.loadOrder(accountId, orderId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isPrintPreview) "Print Preview" else "Order Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isPrintPreview) isPrintPreview = false else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isPrintPreview = !isPrintPreview }) {
                        Icon(
                            if (isPrintPreview) Icons.Default.Edit else Icons.Default.Info,
                            contentDescription = if (isPrintPreview) "Edit Mode" else "Print Preview"
                        )
                    }

                    if (isPrintPreview) {
                        IconButton(onClick = {
                            uiState.order?.let { order ->
                                val data = PrintData(
                                    title = "Order",
                                    subtitle = "Order No: ${order.id} | Date: ${order.date}",
                                    items = uiState.items.map { 
                                        val total = (it.qty.toDoubleOrNull() ?: 0.0) * (it.price.toDoubleOrNull() ?: 0.0)
                                        UtilPrintItem(it.itemName, it.qty, it.price, total.toString())
                                    },
                                    total = order.amount.toString(),
                                    meta = mapOf("Party" to order.partyName, "Status" to "Active")
                                )
                                getPlatformShare().print(data)
                            }
                        }) {
                            Icon(Icons.Default.Info, contentDescription = "Print")
                        }
                        IconButton(onClick = {
                            uiState.order?.let { order ->
                                val data = PrintData(
                                    title = "Order",
                                    subtitle = "Order No: ${order.id} | Date: ${order.date}",
                                    items = uiState.items.map { 
                                        val total = (it.qty.toDoubleOrNull() ?: 0.0) * (it.price.toDoubleOrNull() ?: 0.0)
                                        UtilPrintItem(it.itemName, it.qty, it.price, total.toString())
                                    },
                                    total = order.amount.toString(),
                                    meta = mapOf("Party" to order.partyName, "Status" to "Active")
                                )
                                getPlatformShare().sharePdf(data)
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                    
                    if (!isPrintPreview) {
                        IconButton(onClick = { onNavigateToEdit(orderId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                uiState.order?.let { order ->
                    if (isPrintPreview) {
                        TransactionPrintPreview(
                            title = "Order",
                            details = mapOf(
                                "Order No" to "#${order.id}",
                                "Date" to order.date,
                                "Party" to order.partyName
                            ),
                            items = uiState.items.map { 
                                val total = (it.qty.toDoubleOrNull() ?: 0.0) * (it.price.toDoubleOrNull() ?: 0.0)
                                PrintItem(
                                    name = it.itemName,
                                    qty = it.qty,
                                    price = it.price,
                                    total = total.toString()
                                )
                            },
                            totalAmount = "₹${order.amount}"
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = order.partyName,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (!order.orderNo.isNullOrBlank()) {
                                        Text(
                                            text = "Order No: ${order.orderNo}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                    Text(
                                        text = "Date: ${order.date}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "Items",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            LazyColumn(
                                modifier = Modifier.weight(1f)
                            ) {
                                items(uiState.items) { item ->
                                    ListItem(
                                        headlineContent = { Text(item.itemName) },
                                        supportingContent = { Text("Qty: ${item.qty} x ₹${item.price}") },
                                        trailingContent = { 
                                            val total = (item.qty.toDoubleOrNull() ?: 0.0) * (item.price.toDoubleOrNull() ?: 0.0)
                                            Text("₹$total", fontWeight = FontWeight.Bold) 
                                        }
                                    )
                                    HorizontalDivider()
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Grand Total",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = "₹${order.amount}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
