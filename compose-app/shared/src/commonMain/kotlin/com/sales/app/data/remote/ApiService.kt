package com.sales.app.data.remote

import com.sales.app.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ApiService(
    private val client: HttpClient,
    private val tokenProvider: suspend () -> String?
) {
    // Authentication
    suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun register(request: RegisterRequest): AuthResponse {
        return client.post("register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun logout(): AuthResponse {
        return client.post("logout") {
            addAuthHeader()
        }.body()
    }
    
    suspend fun getCurrentUser(): AuthResponse {
        return client.get("user") {
            addAuthHeader()
        }.body()
    }
    
    // Items
    suspend fun getItems(accountId: Int): ItemsResponse {
        return client.get("items") {
            addAuthHeader()
            parameter("account_id", accountId)
        }.body()
    }
    
    suspend fun getItem(id: Int): ItemResponse {
        return client.get("items/$id") {
            addAuthHeader()
        }.body()
    }
    
    suspend fun createItem(request: ItemRequest): ItemResponse {
        return client.post("items") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateItem(id: Int, request: ItemRequest): ItemResponse {
        return client.put("items/$id") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteItem(id: Int) {
        client.delete("items/$id") {
            addAuthHeader()
        }
    }
    
    suspend fun getUqcs(): UqcsResponse {
        return client.get("uqcs") {
            addAuthHeader()
        }.body()
    }
    
    // Parties
    suspend fun getParties(accountId: Int): PartiesResponse {
        return client.get("parties") {
            addAuthHeader()
            parameter("account_id", accountId)
        }.body()
    }
    
    suspend fun getParty(id: Int): PartyResponse {
        return client.get("parties/$id") {
            addAuthHeader()
        }.body()
    }
    
    suspend fun createParty(request: PartyRequest): PartyResponse {
        return client.post("parties") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateParty(id: Int, request: PartyRequest): PartyResponse {
        return client.put("parties/$id") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteParty(id: Int) {
        client.delete("parties/$id") {
            addAuthHeader()
        }
    }
    
    // Sync
    suspend fun syncMasterData(accountId: Int, timestamp: String): SyncResponse {
        return client.get("sync/master-data") {
            addAuthHeader()
            parameter("account_id", accountId)
            parameter("timestamp", timestamp)
        }.body()
    }
    
    suspend fun fullSync(accountId: Int): SyncResponse {
        return client.get("sync/full") {
            addAuthHeader()
            parameter("account_id", accountId)
        }.body()
    }
    
    suspend fun getSyncStatus(accountId: Int): SyncStatusResponse {
        return client.get("sync/status") {
            addAuthHeader()
            parameter("account_id", accountId)
        }.body()
    }

    // Quotes
    suspend fun getQuotes(accountId: Int): QuotesResponse {
        return client.get("quotes") {
            addAuthHeader()
            parameter("account_id", accountId)
        }.body()
    }
    
    suspend fun getQuote(id: Int): QuoteResponse {
        return client.get("quotes/$id") {
            addAuthHeader()
        }.body()
    }
    
    suspend fun createQuote(request: QuoteRequest): QuoteResponse {
        return client.post("quotes") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateQuote(id: Int, request: QuoteRequest): QuoteResponse {
        return client.put("quotes/$id") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteQuote(id: Int) {
        client.delete("quotes/$id") {
            addAuthHeader()
        }
    }
    
    suspend fun getQuoteItems(accountId: Int): QuoteItemsResponse {
        return client.get("quoteItems") {
            addAuthHeader()
            parameter("account_id", accountId)
        }.body()
    }

    // Accounts
    suspend fun getAccount(id: Int): AccountResponse {
        return client.get("accounts/$id") {
            addAuthHeader()
        }.body()
    }

    suspend fun getUserAccounts(): AccountSelectionResponse {
        return client.get("admin/select-account") {
            addAuthHeader()
        }.body()
    }

    suspend fun selectAccount(accountId: Int): SelectAccountResponse {
        return client.post("admin/select-account") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(SelectAccountRequest(accountId))
        }.body()
    }

    suspend fun updateAccount(id: Int, request: AccountDto): AccountResponse {
        return client.put("accounts/$id") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    private suspend fun HttpRequestBuilder.addAuthHeader() {
        tokenProvider()?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
