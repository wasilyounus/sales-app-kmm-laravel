package com.sales.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sales.app.domain.model.Company

@Composable
fun CompanySelectionDialog(
    companies: List<Company>,
    onCompanySelected: (Int) -> Unit
) {
    Dialog(
        onDismissRequest = { /* Prevent dismissal */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Select Company",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = "You must select a company to proceed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Reuse the switcher logic but maybe simplified or list
                if (companies.isEmpty()) {
                     CircularProgressIndicator()
                } else {
                     CompanySwitcher(
                         companies = companies,
                         selectedCompanyId = -1, // No selection yet
                         onCompanySelected = onCompanySelected
                     )
                }
            }
        }
    }
}
