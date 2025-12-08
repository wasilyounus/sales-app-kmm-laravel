package com.sales.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sales.app.domain.model.Tax

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxSelectionDialog(
    taxes: List<Tax>,
    selectedTaxId: Int?,
    onTaxSelected: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredTaxes = remember(taxes, searchQuery) {
        if (searchQuery.isBlank()) {
            taxes
        } else {
            taxes.filter { tax ->
                tax.schemeName.contains(searchQuery, ignoreCase = true) ||
                tax.tax1Name?.contains(searchQuery, ignoreCase = true) == true ||
                tax.tax2Name?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Tax Scheme",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    placeholder = { Text("Search tax schemes...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Tax List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // None option (always visible)
                    item {
                        TaxItemRow(
                            label = "None",
                            details = "No tax applied",
                            isSelected = selectedTaxId == null,
                            onClick = {
                                onTaxSelected(null)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Filtered tax schemes
                    items(filteredTaxes) { tax ->
                        TaxItemRow(
                            label = tax.schemeName,
                            details = buildTaxDetailsString(tax),
                            isSelected = tax.id == selectedTaxId,
                            onClick = {
                                onTaxSelected(tax.id)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // No results message
                    if (filteredTaxes.isEmpty() && searchQuery.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No matching tax schemes found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaxItemRow(
    label: String,
    details: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
            
            if (details != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun buildTaxDetailsString(tax: Tax): String? {
    val parts = mutableListOf<String>()
    
    tax.tax1Name?.let { name ->
        tax.tax1Val?.let { value ->
            if (value > 0) parts.add("$name ${value}%")
        }
    }
    
    tax.tax2Name?.let { name ->
        tax.tax2Val?.let { value ->
            if (value > 0) parts.add("$name ${value}%")
        }
    }
    
    tax.tax3Name?.let { name ->
        tax.tax3Val?.let { value ->
            if (value > 0) parts.add("$name ${value}%")
        }
    }
    
    tax.tax4Name?.let { name ->
        tax.tax4Val?.let { value ->
            if (value > 0) parts.add("$name ${value}%")
        }
    }
    
    return if (parts.isNotEmpty()) {
        parts.joinToString(" + ")
    } else null
}
