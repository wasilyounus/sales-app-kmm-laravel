package com.sales.app.presentation.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFormScreen(
    accountId: Int,
    paymentId: Int? = null,
    onNavigateBack: () -> Unit,
    viewModel: PaymentFormViewModel
) {
    val parties by viewModel.parties.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedPartyId by remember { mutableStateOf<Int?>(null) }
    var amount by remember { mutableStateOf("") }
    var isReceived by remember { mutableStateOf(true) }
    var comment by remember { mutableStateOf("") }
    var partyExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(accountId) {
        viewModel.loadParties(accountId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (paymentId == null) "Record Payment" else "Update Payment") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            // Payment Type
            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = isReceived,
                    onClick = { isReceived = true },
                    label = { Text("Received (In)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = !isReceived,
                    onClick = { isReceived = false },
                    label = { Text("Paid (Out)") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Party Selection
            ExposedDropdownMenuBox(
                expanded = partyExpanded,
                onExpandedChange = { partyExpanded = !partyExpanded }
            ) {
                OutlinedTextField(
                    value = parties.find { it.id == selectedPartyId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Party") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = partyExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = partyExpanded,
                    onDismissRequest = { partyExpanded = false }
                ) {
                    parties.forEach { party ->
                        DropdownMenuItem(
                            text = { Text(party.name) },
                            onClick = {
                                selectedPartyId = party.id
                                partyExpanded = false
                            }
                        )
                    }
                }
            }

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Comment
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Comment (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (selectedPartyId != null && amount.isNotEmpty()) {
                        viewModel.createPayment(
                            accountId = accountId,
                            partyId = selectedPartyId!!,
                            amount = amount.toDoubleOrNull() ?: 0.0,
                            isReceived = isReceived,
                            comment = comment,
                            onSuccess = onNavigateBack
                        )
                    }
                },
                enabled = !isLoading && selectedPartyId != null && amount.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Payment")
                }
            }
        }
    }
}
