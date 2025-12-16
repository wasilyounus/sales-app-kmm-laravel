package com.sales.app.presentation.items

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sales.app.presentation.components.SalesAppExtendedFab
import com.sales.app.presentation.items.components.ItemCardComponent
import com.sales.app.presentation.items.model.ItemUiModel
import com.sales.app.util.isDesktop

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class, 
    ExperimentalFoundationApi::class)
@Composable

fun ItemsScreen(
    companyId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToItemEdit: (Int) -> Unit,
    onNavigateToCreateItem: () -> Unit,
    viewModel: ItemsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf<ItemUiModel?>(null) }
    var showContextMenu by remember { mutableStateOf<ItemUiModel?>(null) }
    
    LaunchedEffect(companyId) {
        viewModel.loadItems(companyId)
    }
    
    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.onRefresh(companyId) }
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Items",
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
                onClick = onNavigateToCreateItem,
                icon = Icons.Default.Add,
                text = if (isDesktop()) "Add Item" else "Add"
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (isDesktop()) 24.dp else 16.dp, vertical = 16.dp),
                    placeholder = { Text("Search items...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true
                )
                
                when {
                    uiState.isLoading && !uiState.isRefreshing -> {
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
                                TextButton(onClick = { viewModel.onRefresh(companyId) }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    uiState.items.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "No items found",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (uiState.searchQuery.isBlank()) {
                                    TextButton(onClick = onNavigateToCreateItem) {
                                        Text("Add your first item")
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        // Responsive grid layout
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = if (isDesktop()) 350.dp else 300.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = if (isDesktop()) 20.dp else 4.dp,
                                 end = if (isDesktop()) 20.dp else 4.dp,
                                top = 8.dp,
                                bottom = 80.dp // Space for FAB
                            ),
                            horizontalArrangement = Arrangement.spacedBy(if (isDesktop()) 16.dp else 8.dp),
                            verticalArrangement = Arrangement.spacedBy(if (isDesktop()) 16.dp else 8.dp)
                        ) {
                            items(
                                items = uiState.items,
                                key = { it.itemId }
                            ) { item ->
                                Box(
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = null,
                                        fadeOutSpec = null
                                    )
                                ) {
                                    ItemCardComponent(
                                        modifier = Modifier,
                                        itemUiModel = item,
                                        onClick = { onNavigateToItemEdit(item.itemId) },
                                        onLongClick = { showContextMenu = item }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Pull refresh indicator
            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
    
    // Context menu dialog
    showContextMenu?.let { item ->
        AlertDialog(
            onDismissRequest = { showContextMenu = null },
            title = { Text(item.itemName) },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            showContextMenu = null
                            onNavigateToItemEdit(item.itemId)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Edit")
                    }
                    TextButton(
                        onClick = {
                            showContextMenu = null
                            showDeleteDialog = item
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, 
                            tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(8.dp))
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showContextMenu = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { item ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Item") },
            text = { Text("Are you sure you want to delete ${item.itemName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeleteItem(item.itemId)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
