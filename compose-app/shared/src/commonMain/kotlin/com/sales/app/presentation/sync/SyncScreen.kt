package com.sales.app.presentation.sync

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.sales.app.util.isDesktop

@OptIn(ExperimentalMaterial3Api::class, kotlin.time.ExperimentalTime::class)
@Composable
fun SyncScreen(
    companyId: Int,
    onNavigateBack: () -> Unit,
    viewModel: SyncViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Show snackbar for messages
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Sync",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 800.dp) // Constrain width for desktop
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(if (isDesktop()) 32.dp else 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (isDesktop()) 32.dp else 24.dp)
            ) {
                // Header Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(if (isDesktop()) 96.dp else 80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier.size(if (isDesktop()) 48.dp else 40.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Data Synchronization",
                            style = if (isDesktop()) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        uiState.lastSyncTime?.let { timestamp ->
                            val instant = Instant.fromEpochMilliseconds(timestamp.toLong())
                            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                            val formattedDate = "${dateTime.month.name} ${dateTime.day}, ${dateTime.year} ${dateTime.hour}:${dateTime.minute}"
                            
                            Text(
                                text = "Last synced: $formattedDate",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Content Section
                if (isDesktop()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Sync Options Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            SyncOptionsContent(
                                uiState = uiState,
                                onIncrementalSync = { viewModel.syncMasterData(companyId) },
                                onFullSync = { viewModel.fullSync(companyId) }
                            )
                        }
                        
                        // Info Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            SyncInfoContent()
                        }
                    }
                } else {
                    // Mobile Layout - Stacked
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        SyncOptionsContent(
                            uiState = uiState,
                            onIncrementalSync = { viewModel.syncMasterData(companyId) },
                            onFullSync = { viewModel.fullSync(companyId) }
                        )
                    }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        SyncInfoContent()
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncOptionsContent(
    uiState: SyncUiState,
    onIncrementalSync: () -> Unit,
    onFullSync: () -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sync Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Incremental Sync",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Updates only changed data (Faster)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(
                onClick = onIncrementalSync,
                enabled = !uiState.isSyncing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sync Now")
                }
            }
        }
        
        HorizontalDivider()
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Full Sync",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Downloads all data from server (Slower)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            OutlinedButton(
                onClick = onFullSync,
                enabled = !uiState.isSyncing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Sync, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Full Sync")
            }
        }
    }
}

@Composable
private fun SyncInfoContent() {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "ℹ️ Sync Information",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "• Incremental sync is recommended for daily use as it's faster and uses less data.\n\n" +
                    "• Use Full Sync if you notice missing data or inconsistencies.\n\n" +
                    "• Ensure you have a stable internet connection before starting a sync process.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
