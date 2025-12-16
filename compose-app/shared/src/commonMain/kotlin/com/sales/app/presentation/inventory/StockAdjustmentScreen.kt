package com.sales.app.presentation.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sales.app.domain.model.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustmentDialog(
    companyId: Int,
    onDismissRequest: () -> Unit,
    onAdjustmentSaved: () -> Unit,
    viewModel: StockAdjustmentViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showItemSelection by remember { mutableStateOf(false) }

    LaunchedEffect(companyId) {
        viewModel.loadItems(companyId)
    }
    
    // Handle successful save (you might need to expose a simplified event for this in VM or check loading state transition)
    // For now, let's rely on the caller to refresh, but ideally the VM should signal success.
    // Since onAdjustmentSaved is passed to saveAdjustment, we assume VM calls it or we pass a wrapper.
    // The previous implementation took onNavigateBack as a success callback.
    
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Adjust Stock",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                HorizontalDivider()
                
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Item Selection Field
                        Text("Item", style = MaterialTheme.typography.labelMedium)
                        
                        val selectedItemName = uiState.items.find { it.id == uiState.itemId }?.name ?: "Select Item"
                        
                        OutlinedTextField(
                            value = selectedItemName,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false, // We handle click on the container or box
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showItemSelection = true },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                            trailingIcon = {
                                Icon(Icons.Default.Search, "Select Item")
                            }
                        )
                        if (!uiState.isItemValid) {
                            Text("Please select an item", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }

                        // Adjustment Type
                        Text("Adjustment Type", style = MaterialTheme.typography.labelMedium)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilterChip(
                                selected = uiState.type == "IN",
                                onClick = { viewModel.onTypeChange("IN") },
                                label = { Text("Stock In (+)") },
                                leadingIcon = {
                                    if (uiState.type == "IN") {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp)) // Placeholder checkmark
                                    }
                                }
                            )
                            FilterChip(
                                selected = uiState.type == "OUT",
                                onClick = { viewModel.onTypeChange("OUT") },
                                label = { Text("Stock Out (-)") },
                                leadingIcon = {
                                    if (uiState.type == "OUT") {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                        }

                        // Quantity
                        OutlinedTextField(
                            value = uiState.qty,
                            onValueChange = { viewModel.onQtyChange(it) },
                            label = { Text("Quantity") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = !uiState.isQtyValid
                        )
                        if (!uiState.isQtyValid) {
                            Text("Enter a valid quantity", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }

                        // Reason
                        OutlinedTextField(
                            value = uiState.reason,
                            onValueChange = { viewModel.onReasonChange(it) },
                            label = { Text("Reason (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        
                        uiState.error?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                    
                    // Footer Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { 
                                viewModel.saveAdjustment(companyId) {
                                    onAdjustmentSaved()
                                    onDismissRequest()
                                } 
                            },
                            enabled = !uiState.isSaving
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("Save Adjustment")
                        }
                    }
                }
            }
        }
    }

    if (showItemSelection) {
        ItemSelectionDialog(
            items = uiState.items,
            onDismissRequest = { showItemSelection = false },
            onItemSelected = { item ->
                viewModel.onItemChange(item.id)
                showItemSelection = false
            }
        )
    }
}

@Composable
fun ItemSelectionDialog(
    items: List<Item>,
    onDismissRequest: () -> Unit,
    onItemSelected: (Item) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(searchQuery, items) {
        if (searchQuery.isBlank()) items
        else items.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Select Item",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Items") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems) { item ->
                        ListItem(
                            headlineContent = { Text(item.name) },
                            supportingContent = { Text("ID: ${item.id} | Brand: ${item.brand ?: "-"}") },
                            modifier = Modifier
                                .clickable { onItemSelected(item) }
                                .fillMaxWidth(),
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                    if (filteredItems.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No items found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
