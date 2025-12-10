package com.sales.app.presentation.pricelists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sales.app.domain.model.PriceListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceListDetailScreen(
    accountId: Int,
    priceListId: Long,
    onNavigateBack: () -> Unit,
    viewModel: PriceListDetailViewModel
) {
    val priceList by viewModel.priceList.collectAsState()
    val availableItems by viewModel.availableItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItemId by remember { mutableStateOf<Long?>(null) }
    var priceInput by remember { mutableStateOf("") }
    var itemExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(accountId, priceListId) {
        if (priceListId != -1L) {
            viewModel.loadPriceList(priceListId)
        }
        viewModel.loadAvailableItems(accountId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (priceListId == -1L) "Create Price List" else "Update Price List") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        if (isLoading && priceList == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(priceList?.items ?: emptyList()) { item ->
                    PriceListItemCard(
                        item = item,
                        onDelete = { viewModel.removeItem(priceListId, item.itemId) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Item to Price List") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Item Selection
                        ExposedDropdownMenuBox(
                            expanded = itemExpanded,
                            onExpandedChange = { itemExpanded = !itemExpanded }
                        ) {
                            OutlinedTextField(
                                value = availableItems.find { it.id.toLong() == selectedItemId }?.name ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Item") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = itemExpanded,
                                onDismissRequest = { itemExpanded = false }
                            ) {
                                availableItems.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item.name) },
                                        onClick = {
                                            selectedItemId = item.id.toLong()
                                            itemExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = priceInput,
                            onValueChange = { priceInput = it },
                            label = { Text("Custom Price") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (selectedItemId != null && priceInput.isNotEmpty()) {
                                viewModel.addItem(
                                    priceListId,
                                    selectedItemId!!,
                                    priceInput.toDoubleOrNull() ?: 0.0
                                )
                                showAddDialog = false
                                selectedItemId = null
                                priceInput = ""
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PriceListItemCard(
    item: PriceListItem,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = item.itemName ?: "Unknown Item", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Custom Price: ₹${item.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
