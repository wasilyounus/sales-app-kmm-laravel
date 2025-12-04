package com.sales.app.presentation.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockAdjustmentScreen(
    accountId: Int,
    onNavigateBack: () -> Unit,
    viewModel: StockAdjustmentViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(accountId) {
        viewModel.loadItems(accountId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adjust Stock") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Item Selection (Simple Dropdown for now)
                    Text("Select Item", style = MaterialTheme.typography.labelLarge)
                    // Note: A proper dropdown or search dialog would be better here, 
                    // but keeping it simple for this iteration.
                    // In a real app, use the ItemPicker component.
                    
                    // Using a simple list of radio buttons for item selection if list is small, 
                    // or just a placeholder text if list is large.
                    // For now, let's assume a small list and use a scrollable column of radio buttons inside a box
                    // or just a text field to enter ID (temporary) or better:
                    // A ExposedDropdownMenuBox would be ideal but requires more boilerplate.
                    // Let's implement a simple column of selectable items for MVP.
                    
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    ) {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
                            uiState.items.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = item.id == uiState.itemId,
                                        onClick = { viewModel.onItemChange(item.id) }
                                    )
                                    Text(text = item.name, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    }
                    if (!uiState.isItemValid) {
                        Text("Please select an item", color = MaterialTheme.colorScheme.error)
                    }

                    // Type Selection
                    Text("Adjustment Type", style = MaterialTheme.typography.labelLarge)
                    Row {
                        RadioButton(
                            selected = uiState.type == "IN",
                            onClick = { viewModel.onTypeChange("IN") }
                        )
                        Text("Stock In (Add)", modifier = Modifier.align(Alignment.CenterVertically))
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = uiState.type == "OUT",
                            onClick = { viewModel.onTypeChange("OUT") }
                        )
                        Text("Stock Out (Remove)", modifier = Modifier.align(Alignment.CenterVertically))
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
                        Text("Enter a valid quantity", color = MaterialTheme.colorScheme.error)
                    }

                    // Reason
                    OutlinedTextField(
                        value = uiState.reason,
                        onValueChange = { viewModel.onReasonChange(it) },
                        label = { Text("Reason (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.saveAdjustment(accountId, onNavigateBack) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Save Adjustment")
                        }
                    }
                    
                    uiState.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
