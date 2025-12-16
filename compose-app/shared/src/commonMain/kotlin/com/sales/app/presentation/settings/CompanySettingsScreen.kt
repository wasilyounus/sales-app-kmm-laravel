package com.sales.app.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sales.app.util.TimeProvider
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySettingsScreen(
    companyId: Int,
    onNavigateBack: () -> Unit,
    viewModel: CompanySettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(companyId) {
        viewModel.loadCompany(companyId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Company Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            } else if (uiState.company == null) {
                Text(
                    text = "No company data available",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                CompanySettingsContent(
                    company = uiState.company!!,
                    isSaving = uiState.isSaving,
                    error = uiState.error,
                    successMessage = uiState.successMessage,
                    onFinancialYearStartChange = { dateTime ->
                        viewModel.updateFinancialYearStart(dateTime)
                    },
                    onTaxationTypeChange = { taxationType ->
                        viewModel.updateTaxationType(taxationType)
                    },
                    onDefaultTaxChange = { taxId ->
                        viewModel.updateDefaultTax(taxId)
                    },
                    onAddressChange = viewModel::updateAddress,
                    onCallChange = viewModel::updateCall,
                    onWhatsappChange = viewModel::updateWhatsapp,
                    onFooterContentChange = viewModel::updateFooterContent,
                    onSignatureChange = viewModel::updateSignature,
                    onEnableDeliveryNotesChange = viewModel::updateEnableDeliveryNotes,
                    onEnableGrnsChange = viewModel::updateEnableGrns,
                    onDarkModeChange = viewModel::updateDarkMode,
                    onClearMessages = viewModel::clearMessages,
                    taxes = uiState.taxes
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanySettingsContent(
    company: com.sales.app.domain.model.Company,
    isSaving: Boolean,
    error: String?,
    successMessage: String?,
    onFinancialYearStartChange: (String) -> Unit,
    onTaxationTypeChange: (Int) -> Unit,
    onDefaultTaxChange: (Int) -> Unit,
    onAddressChange: (String) -> Unit,
    onCallChange: (String) -> Unit,
    onWhatsappChange: (String) -> Unit,
    onFooterContentChange: (String) -> Unit,
    onSignatureChange: (Boolean) -> Unit,
    onEnableDeliveryNotesChange: (Boolean) -> Unit,
    onEnableGrnsChange: (Boolean) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onClearMessages: () -> Unit,
    taxes: List<com.sales.app.domain.model.Tax>
) {
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showTaxDropdown by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Account Info Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Company Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                InfoRow(label = "Name", value = company.name)
                InfoRow(label = "Formatted Name", value = company.nameFormatted)
                company.desc?.let { InfoRow(label = "Description", value = it) }
                
                HorizontalDivider()
                
                // Contact Info
                OutlinedTextField(
                    value = company.address ?: "",
                    onValueChange = onAddressChange,
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving,
                    minLines = 2
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = company.call ?: "",
                        onValueChange = onCallChange,
                        label = { Text("Phone") },
                        modifier = Modifier.weight(1f),
                        enabled = !isSaving,
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = company.whatsapp ?: "",
                        onValueChange = onWhatsappChange,
                        label = { Text("WhatsApp") },
                        modifier = Modifier.weight(1f),
                        enabled = !isSaving,
                        singleLine = true
                    )
                }
                
                HorizontalDivider()
                
                InfoRow(label = "Country", value = company.country ?: "India")
                company.state?.let { InfoRow(label = "State", value = it) }
                company.taxNumber?.let { 
                    InfoRow(label = if (company.country == "India") "GST Number" else "Tax Number", value = it) 
                }
                
                // Footer Content
                OutlinedTextField(
                    value = company.footerContent ?: "",
                    onValueChange = onFooterContentChange,
                    label = { Text("Footer Content (Invoice)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving,
                    minLines = 2
                )
                
                // Signature Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable Signature",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = company.signature == "1" || company.signature == "true",
                        onCheckedChange = onSignatureChange,
                        enabled = !isSaving
                    )
                }
            }
        }


        // Contacts Section
        if (company.contacts.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "People / Contacts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    company.contacts.forEach { contact ->
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (contact.isPrimary) {
                                    Surface(
                                        shape = MaterialTheme.shapes.extraSmall,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ) {
                                        Text(
                                            text = "Primary",
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            
                            contact.designation?.takeIf { it.isNotBlank() }?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            val details = listOfNotNull(
                                contact.phone?.takeIf { it.isNotBlank() }, 
                                contact.email?.takeIf { it.isNotBlank() }
                            ).joinToString(" • ")
                            
                            if (details.isNotEmpty()) {
                                Text(
                                    text = details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }

        // Features Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Features",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Delivery Notes Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Delivery Notes",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "If disabled, Delivery Notes are automatically created with Sales",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = company.enableDeliveryNotes,
                        onCheckedChange = onEnableDeliveryNotesChange,
                        enabled = !isSaving
                    )
                }

                HorizontalDivider()

                // GRN Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable GRN",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "If disabled, GRNs are automatically created with Purchases",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = company.enableGrns,
                        onCheckedChange = onEnableGrnsChange,
                        enabled = !isSaving
                    )
                }

                HorizontalDivider()

                // Dark Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Enable dark theme for the application (Experimental)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = company.darkMode,
                        onCheckedChange = onDarkModeChange,
                        enabled = !isSaving
                    )
                }
            }
        }
        
        // Tax Settings Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tax Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Taxation Type",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                // Taxation Type Radio Buttons
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TaxationTypeOption(
                        label = "No Tax / Composition",
                        description = "No tax or composition scheme",
                        selected = company.taxationType == 1,
                        onClick = { onTaxationTypeChange(1) },
                        enabled = !isSaving
                    )
                    
                    TaxationTypeOption(
                        label = "Inclusive Tax",
                        description = "Tax is included in the price",
                        selected = company.taxationType == 2,
                        onClick = { onTaxationTypeChange(2) },
                        enabled = !isSaving
                    )
                    
                    TaxationTypeOption(
                        label = "Exclusive Tax",
                        description = "Tax is added on top of the price",
                        selected = company.taxationType == 3,
                        onClick = { onTaxationTypeChange(3) },
                        enabled = !isSaving
                    )
                }
                
                // Default Tax Selection (only show if not "No Tax")
                if (company.taxationType != 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = showTaxDropdown,
                        onExpandedChange = { if (!isSaving) showTaxDropdown = !showTaxDropdown }
                    ) {
                        val selectedTax = taxes.find { it.id == company.defaultTaxId }
                        val displayText = selectedTax?.let { "${it.schemeName} (${(it.tax1Val ?: 0.0) + (it.tax2Val ?: 0.0) + (it.tax3Val ?: 0.0) + (it.tax4Val ?: 0.0)}%)" } ?: "Select Default Tax"
                        
                        // Filter taxes by account country
                        val filteredTaxes = taxes.filter { tax ->
                            tax.country == null || tax.country == company.country
                        }
                        
                        OutlinedTextField(
                            value = displayText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Default Tax") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTaxDropdown) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                            enabled = !isSaving
                        )
                        
                        ExposedDropdownMenu(
                            expanded = showTaxDropdown,
                            onDismissRequest = { showTaxDropdown = false }
                        ) {
                            filteredTaxes.forEach { tax ->
                                DropdownMenuItem(
                                    text = { 
                                        Text("${tax.schemeName} (${(tax.tax1Val ?: 0.0) + (tax.tax2Val ?: 0.0) + (tax.tax3Val ?: 0.0) + (tax.tax4Val ?: 0.0)}%)") 
                                    },
                                    onClick = {
                                        onDefaultTaxChange(tax.id)
                                        showTaxDropdown = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = "Select the default tax rate for new items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Financial Year Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Financial Year",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Display current financial year start
                val displayDateTime = company.financialYearStart?.let { dateTimeStr ->
                    try {
                        // Parse ISO datetime and format for display
                        val parts = dateTimeStr.split(" ")
                        if (parts.size == 2) {
                            val dateParts = parts[0].split("-")
                            val timeParts = parts[1].split(":")
                            if (dateParts.size == 3 && timeParts.size >= 2) {
                                "${dateParts[2]}/${dateParts[1]}/${dateParts[0]} ${timeParts[0]}:${timeParts[1]}"
                            } else dateTimeStr
                        } else dateTimeStr
                    } catch (e: Exception) {
                        dateTimeStr
                    }
                } ?: "Not set"
                
                OutlinedTextField(
                    value = displayDateTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Financial Year Start (DD/MM/YYYY HH:MM)") },
                    trailingIcon = {
                        IconButton(onClick = { showDateTimePicker = true }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date & Time")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSaving
                )
                
                Text(
                    text = "Set the start date and time of your financial year. This will be stored in UTC.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Messages
        error?.let { err ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onClearMessages) {
                        Text("Dismiss")
                    }
                }
            }
        }
        
        successMessage?.let { msg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onClearMessages) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
    
    if (showDateTimePicker) {
        DateTimePickerDialog(
            initialDateTime = company.financialYearStart,
            onDismiss = { showDateTimePicker = false },
            onConfirm = { dateTime ->
                onFinancialYearStartChange(dateTime)
                showDateTimePicker = false
            }
        )
    }
}

@Composable
private fun TaxationTypeOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.secondary
            )
        } else null,
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                enabled = enabled
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun DateTimePickerDialog(
    initialDateTime: String?,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    // Parse initial datetime if available
    val initialMillis = initialDateTime?.let { dateTimeStr ->
        try {
            // Expecting format: YYYY-MM-DD HH:MM:SS
            val instant = Instant.parse(dateTimeStr.replace(" ", "T") + "Z")
            instant.toEpochMilliseconds()
        } catch (e: Exception) {
            null
        }
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis ?: TimeProvider.now().toEpochMilliseconds()
    )
    
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Financial Year Start") },
        text = {
            Column {
                DatePicker(state = datePickerState)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Time (UTC)",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour picker
                    OutlinedTextField(
                        value = selectedHour.toString().padStart(2, '0'),
                        onValueChange = { 
                            it.toIntOrNull()?.let { hour ->
                                if (hour in 0..23) selectedHour = hour
                            }
                        },
                        label = { Text("Hour") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(":", style = MaterialTheme.typography.headlineSmall)
                    
                    // Minute picker
                    OutlinedTextField(
                        value = selectedMinute.toString().padStart(2, '0'),
                        onValueChange = { 
                            it.toIntOrNull()?.let { minute ->
                                if (minute in 0..59) selectedMinute = minute
                            }
                        },
                        label = { Text("Minute") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.fromEpochMilliseconds(millis)
                        val dateTime = instant.toLocalDateTime(TimeZone.UTC)
                        
                        // Format as YYYY-MM-DD HH:MM:SS
                        val formatted = "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')} ${selectedHour.toString().padStart(2, '0')}:${selectedMinute.toString().padStart(2, '0')}:00"
                        onConfirm(formatted)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
