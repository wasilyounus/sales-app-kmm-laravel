package com.sales.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sales.app.data.local.AppDatabase
import com.sales.app.data.remote.ApiService
import com.sales.app.data.repository.AuthRepository
import com.sales.app.data.repository.ItemRepository
import com.sales.app.data.repository.PartyRepository
import com.sales.app.data.repository.SyncRepository
import com.sales.app.data.repository.QuoteRepository
import com.sales.app.domain.usecase.*
import com.sales.app.presentation.home.HomeViewModel
import com.sales.app.presentation.items.ItemsViewModel
import com.sales.app.presentation.items.ItemFormViewModel
import com.sales.app.presentation.login.LoginViewModel
import com.sales.app.presentation.parties.PartiesViewModel
import com.sales.app.presentation.register.RegisterViewModel
import com.sales.app.presentation.sync.SyncViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class AppContainer(
    private val database: AppDatabase,
    private val httpClient: HttpClient,
    private val dataStore: DataStore<Preferences>
) {
    // Services
    private val apiService = ApiService(httpClient) {
        // Token Provider
        dataStore.data.map { preferences ->
            preferences[androidx.datastore.preferences.core.stringPreferencesKey("auth_token")]
        }.firstOrNull()
    }
    
    // Repositories
    val authRepository = AuthRepository(apiService, database.userDao(), dataStore)
    val itemRepository = ItemRepository(apiService, database.itemDao())
    val partyRepository = PartyRepository(apiService, database.partyDao(), database.addressDao())
    val syncRepository = SyncRepository(apiService, database.itemDao(), database.partyDao(), database.syncDao())
    val quoteRepository = QuoteRepository(apiService, database.quoteDao(), database.quoteItemDao())
    val accountRepository = com.sales.app.data.repository.AccountRepositoryImpl(apiService, database.accountDao())

    // Use Cases
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val getItemsUseCase = GetItemsUseCase(itemRepository)
    val searchItemsUseCase = SearchItemsUseCase(itemRepository)
    val createItemUseCase = CreateItemUseCase(itemRepository)
    val updateItemUseCase = UpdateItemUseCase(itemRepository)
    val getItemByIdUseCase = GetItemByIdUseCase(itemRepository)
    val getUqcsUseCase = GetUqcsUseCase(itemRepository)
    
    val getPartiesUseCase = GetPartiesUseCase(partyRepository)
    val searchPartiesUseCase = SearchPartiesUseCase(partyRepository)
    val createPartyUseCase = CreatePartyUseCase(partyRepository)
    val updatePartyUseCase = UpdatePartyUseCase(partyRepository)
    val getPartyByIdUseCase = GetPartyByIdUseCase(partyRepository)
    
    val getQuotesUseCase = GetQuotesUseCase(quoteRepository)
    val getQuoteByIdUseCase = GetQuoteByIdUseCase(quoteRepository)
    val createQuoteUseCase = CreateQuoteUseCase(quoteRepository)
    val updateQuoteUseCase = UpdateQuoteUseCase(quoteRepository)
    val deleteQuoteUseCase = DeleteQuoteUseCase(quoteRepository)
    val syncQuotesUseCase = SyncQuotesUseCase(quoteRepository)
    
    val getAccountUseCase = GetAccountUseCase(accountRepository)
    val updateAccountUseCase = UpdateAccountUseCase(accountRepository)
    val fetchAccountUseCase = FetchAccountUseCase(accountRepository)
    
    val syncMasterDataUseCase = SyncMasterDataUseCase(syncRepository)
    val fullSyncUseCase = FullSyncUseCase(syncRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    
    // ViewModel Factories
    fun createLoginViewModel() = LoginViewModel(loginUseCase)
    fun createRegisterViewModel() = RegisterViewModel(registerUseCase)
    fun createHomeViewModel() = HomeViewModel(
        logoutUseCase,
        getItemsUseCase,
        getPartiesUseCase,
        getQuotesUseCase
    )
    fun createItemsViewModel() = ItemsViewModel(getItemsUseCase, syncMasterDataUseCase, getUqcsUseCase)
    fun createItemFormViewModel() = ItemFormViewModel(
        createItemUseCase,
        updateItemUseCase,
        getItemByIdUseCase,
        getUqcsUseCase
    )
    fun createPartiesViewModel() = PartiesViewModel(getPartiesUseCase, searchPartiesUseCase)
    fun createPartyFormViewModel() = com.sales.app.presentation.parties.PartyFormViewModel(
        createPartyUseCase,
        updatePartyUseCase,
        getPartyByIdUseCase
    )
    fun createQuotesViewModel() = com.sales.app.presentation.quotes.QuotesViewModel(
        getQuotesUseCase,
        getPartiesUseCase,
        syncQuotesUseCase
    )
    fun createQuoteFormViewModel() = com.sales.app.presentation.quotes.QuoteFormViewModel(
        createQuoteUseCase,
        updateQuoteUseCase,
        getQuoteByIdUseCase,
        getPartiesUseCase,
        getItemsUseCase
    )
    fun createQuoteViewViewModel() = com.sales.app.presentation.quotes.QuoteViewViewModel(
        getQuoteByIdUseCase,
        getPartyByIdUseCase
    )
    fun createAccountSettingsViewModel() = com.sales.app.presentation.settings.AccountSettingsViewModel(
        getAccountUseCase,
        updateAccountUseCase,
        fetchAccountUseCase
    )
    fun createSyncViewModel() = SyncViewModel(syncMasterDataUseCase, fullSyncUseCase)
}
