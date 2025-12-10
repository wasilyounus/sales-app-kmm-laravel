package com.sales.app.presentation.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sales.app.domain.model.SyncType
import com.sales.app.domain.usecase.FullSyncUseCase
import com.sales.app.domain.usecase.SyncDataUseCase
import com.sales.app.domain.usecase.SyncMasterDataUseCase
import com.sales.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.sales.app.util.TimeProvider
import kotlinx.datetime.Instant

data class SyncUiState(
    val isSyncing: Boolean = false,
    val lastSyncTime: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)

@OptIn(kotlin.time.ExperimentalTime::class)
class SyncViewModel(
    private val syncDataUseCase: SyncDataUseCase,
    private val syncMasterDataUseCase: SyncMasterDataUseCase,
    private val fullSyncUseCase: FullSyncUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SyncUiState())
    val uiState: StateFlow<SyncUiState> = _uiState.asStateFlow()
    
    fun syncData(accountId: Int, types: List<SyncType>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null, successMessage = null) }
            
            when (val result = syncDataUseCase(accountId, types)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            successMessage = "Sync completed successfully",
                            lastSyncTime = TimeProvider.now().toEpochMilliseconds().toString()
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isSyncing = true) }
                }
            }
        }
    }
    
    fun syncMasterData(accountId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null, successMessage = null) }
            
            when (val result = syncMasterDataUseCase(accountId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            successMessage = "Sync completed successfully",
                            lastSyncTime = TimeProvider.now().toEpochMilliseconds().toString()
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isSyncing = true) }
                }
            }
        }
    }
    
    fun fullSync(accountId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, error = null, successMessage = null) }
            
            when (val result = fullSyncUseCase(accountId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            successMessage = "Full sync completed successfully",
                            lastSyncTime = TimeProvider.now().toEpochMilliseconds().toString()
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isSyncing = true) }
                }
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
