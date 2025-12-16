package com.sales.app.presentation.parties

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sales.app.presentation.components.SalesAppExtendedFab
import com.sales.app.domain.model.Party
import com.sales.app.util.isDesktop

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PartiesScreen(
    companyId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToPartyEdit: (Int) -> Unit,
    onNavigateToCreateParty: () -> Unit,
    viewModel: PartiesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(companyId) {
        viewModel.loadParties(companyId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Parties",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            SalesAppExtendedFab(
                onClick = onNavigateToCreateParty,
                icon = Icons.Default.Add,
                text = if (isDesktop()) "Add Party" else "Add"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isDesktop()) 24.dp else 16.dp, vertical = 16.dp),
                placeholder = { Text("Search parties...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = uiState.error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            TextButton(onClick = { viewModel.loadParties(companyId) }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                uiState.parties.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "No parties found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (uiState.searchQuery.isBlank()) {
                                TextButton(onClick = onNavigateToCreateParty) {
                                    Text("Add your first party")
                                }
                            }
                        }
                    }
                }
                else -> {
                    if (isDesktop()) {
                        // Desktop Grid Layout
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 350.dp),
                            modifier = Modifier.fillMaxSize(),

                            contentPadding = PaddingValues(
                                start = if (isDesktop()) 20.dp else 4.dp,
                                end = if (isDesktop()) 20.dp else 4.dp,
                                top = 8.dp,
                                bottom = 80.dp // Space for FAB
                            ),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = uiState.parties,
                                key = { it.id }
                            ) { party ->
                                PartyCard(
                                    party = party,
                                    onClick = { onNavigateToPartyEdit(party.id) }
                                )
                            }
                        }
                    } else {
                        // Mobile List Layout
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = if (isDesktop()) 20.dp else 4.dp,
                                end = if (isDesktop()) 20.dp else 4.dp,
                                top = 8.dp,
                                bottom = 80.dp // Space for FAB
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.parties,
                                key = { it.id }
                            ) { party ->
                                PartyCard(
                                    party = party,
                                    onClick = { onNavigateToPartyEdit(party.id) }
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
fun PartyCard(
    party: Party,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = party.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                
                party.taxNumber?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Tax #: $it",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                party.phone?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                party.email?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
