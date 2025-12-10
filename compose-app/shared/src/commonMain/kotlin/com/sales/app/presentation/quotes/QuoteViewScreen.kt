package com.sales.app.presentation.quotes

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
fun QuoteViewScreen(
    accountId: Int,
    quoteId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: QuoteViewViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var isPrintPreview by remember { mutableStateOf(false) }
    
    LaunchedEffect(accountId, quoteId) {
        viewModel.loadQuote(accountId, quoteId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isPrintPreview) "Print Preview" else "Quote Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isPrintPreview) isPrintPreview = false else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Toggle Print Preview
                    IconButton(onClick = { isPrintPreview = !isPrintPreview }) {
                        Icon(
                            if (isPrintPreview) Icons.Default.Edit else Icons.Default.Info, // Using Info icon as placeholder for "Print/View" toggle if no specific print icon
                            contentDescription = if (isPrintPreview) "Edit Mode" else "Print Preview"
                        )
                    }

                    if (isPrintPreview) {
                        IconButton(onClick = {
                            uiState.quote?.let { quote ->
                                val data = PrintData(
                                    title = "Quote",
                                    subtitle = "Quote No: ${quote.id} | Date: ${quote.date}",
                                    items = uiState.items.map { 
                                        val total = (it.qty.toDoubleOrNull() ?: 0.0) * (it.price.toDoubleOrNull() ?: 0.0)
                                        UtilPrintItem(it.itemName, it.qty, it.price, total.toString())
                                    },
                                    total = quote.amount.toString(),
                                    meta = mapOf("Party" to quote.partyName, "Status" to "Pending")
                                )
                                getPlatformShare().print(data)
                            }
                        }) {
                            Icon(Icons.Default.Info, contentDescription = "Print")
                        }
                        IconButton(onClick = {
                            uiState.quote?.let { quote ->
                                val data = PrintData(
                                    title = "Quote",
                                    subtitle = "Quote No: ${quote.id} | Date: ${quote.date}",
                                    items = uiState.items.map { 
                                        val total = (it.qty.toDoubleOrNull() ?: 0.0) * (it.price.toDoubleOrNull() ?: 0.0)
                                        UtilPrintItem(it.itemName, it.qty, it.price, total.toString())
                                    },
                                    total = quote.amount.toString(),
                                    meta = mapOf("Party" to quote.partyName, "Status" to "Pending")
                                )
                                getPlatformShare().sharePdf(data)
                            }
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                    
                    if (!isPrintPreview) {
                        IconButton(onClick = { onNavigateToEdit(quoteId) }) {
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
                uiState.quote?.let { quote ->
                    if (isPrintPreview) {
                        TransactionPrintPreview(
                            title = "Quote",
                            details = mapOf(
                                "Quote No" to "#${quote.id}",
                                "Date" to quote.date,
                                "Party" to quote.partyName
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
                            totalAmount = "₹${quote.amount}"
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Header Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = quote.partyName,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (!quote.quoteNo.isNullOrBlank()) {
                                        Text(
                                            text = "Quote No: ${quote.quoteNo}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                    Text(
                                        text = "Date: ${quote.date}",
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
                                        text = "₹${quote.amount}",
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
