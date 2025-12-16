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
        val response = client.post("login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        
        if (response.status == HttpStatusCode.UnprocessableEntity) {
            val error = response.body<LaravelValidationError>()
            throw Exception(error.message)
        }
        
        return response.body()
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
    suspend fun getItems(companyId: Int): ItemsResponse {
        return client.get("items") {
            addAuthHeader()
            parameter("company_id", companyId)
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
    
    suspend fun getTaxes(): TaxesResponse {
        return client.get("taxes") {
            addAuthHeader()
        }.body()
    }
    
    // Parties
    suspend fun getParties(companyId: Int): PartiesResponse {
        return client.get("parties") {
            addAuthHeader()
            parameter("company_id", companyId)
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
    suspend fun syncMasterData(companyId: Int, timestamp: String): SyncResponse {
        return client.get("sync/master-data") {
            addAuthHeader()
            parameter("company_id", companyId)
            parameter("timestamp", timestamp)
        }.body()
    }
    
    suspend fun fullSync(companyId: Int): SyncResponse {
        return client.get("sync/full") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }
    
    suspend fun getSyncStatus(companyId: Int): SyncStatusResponse {
        return client.get("sync/status") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }

    // Quotes
    suspend fun getQuotes(companyId: Int): QuotesResponse {
        return client.get("quotes") {
            addAuthHeader()
            parameter("company_id", companyId)
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
    
    suspend fun getQuoteItems(companyId: Int): QuoteItemsResponse {
        return client.get("quoteItems") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }

    // Accounts
    suspend fun getCompany(id: Int): CompanyResponse {
        return client.get("companies/$id") {
            addAuthHeader()
        }.body()
    }

    suspend fun getCompanies(): CompanySelectionResponse {
        return client.get("admin/select-account") {
            addAuthHeader()
        }.body()
    }
    
    // Modules
    suspend fun getModules(): List<ModuleDto> {
        return client.get("modules") {
            addAuthHeader()
        }.body()
    }

    suspend fun selectCompany(companyId: Int): SelectCompanyResponse {
        return client.post("admin/select-account") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(SelectCompanyRequest(companyId))
        }.body()
    }

    suspend fun updateCompany(id: Int, request: CompanyDto): CompanyResponse {
        return client.put("companies/$id") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    // Sales
    suspend fun getSales(companyId: Int): SalesResponse {
        return client.get("sales") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }
    
    suspend fun getSale(id: Int): SaleResponse {
        return client.get("sales/$id") {
            addAuthHeader()
        }.body()
    }
    
    suspend fun createSale(request: SaleRequest): SaleResponse {
        return client.post("sales") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateSale(id: Int, request: SaleRequest): SaleResponse {
        return client.put("sales/$id") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteSale(id: Int) {
        client.delete("sales/$id") {
            addAuthHeader()
        }
    }
    
    suspend fun getSaleItems(companyId: Int): SaleItemsResponse {
        return client.get("saleItems") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }

    // Orders
    suspend fun getOrders(companyId: Int): OrdersResponse {
        return client.get("orders") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }
    
    suspend fun getOrder(id: Int): OrderResponse {
        return client.get("orders/$id") {
            addAuthHeader()
        }.body()
    }
    
    suspend fun createOrder(request: OrderRequest): OrderResponse {
        return client.post("orders") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updateOrder(id: Int, request: OrderRequest): OrderResponse {
        return client.put("orders/$id") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deleteOrder(id: Int) {
        client.delete("orders/$id") {
            addAuthHeader()
        }
    }
    
    suspend fun getOrderItems(companyId: Int): OrderItemsResponse {
        return client.get("orderItems") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }

    // Purchases
    suspend fun getPurchases(companyId: Int): PurchasesResponse {
        return client.get("purchases") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }
    
    suspend fun getPurchase(id: Int): PurchaseResponse {
        return client.get("purchases/$id") {
            addAuthHeader()
        }.body()
    }
    
    suspend fun createPurchase(request: PurchaseRequest): PurchaseResponse {
        return client.post("purchases") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun updatePurchase(id: Int, request: PurchaseRequest): PurchaseResponse {
        return client.put("purchases/$id") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
    
    suspend fun deletePurchase(id: Int) {
        client.delete("purchases/$id") {
            addAuthHeader()
        }
    }
    
    suspend fun getPurchaseItems(companyId: Int): PurchaseItemsResponse {
        return client.get("purchaseItems") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }

    // Payments
    suspend fun getTransactions(companyId: Int, page: Int = 1, search: String? = null): TransactionsResponse {
        return client.get("payments") {
            addAuthHeader()
            parameter("company_id", companyId)
            parameter("page", page)
            if (search != null) parameter("search", search)
        }.body()
    }

    suspend fun createTransaction(request: TransactionRequest) {
        client.post("payments") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    // Price Lists
    suspend fun getPriceLists(companyId: Int): PriceListsResponse {
        return client.get("price-lists") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }

    suspend fun getPriceList(id: Long): PriceListDto {
        return client.get("price-lists/$id") {
            addAuthHeader()
        }.body()
    }

    suspend fun createPriceList(request: PriceListRequest) {
        client.post("price-lists") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun updatePriceListItems(id: Long, request: UpdatePriceListItemsRequest) {
        client.post("price-lists/$id/items") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun deletePriceList(id: Long) {
        client.delete("price-lists/$id") {
            addAuthHeader()
        }
    }

    // Delivery Notes
    suspend fun getDeliveryNotes(companyId: Int): DeliveryNotesResponse {
        return client.get("delivery-notes") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }

    suspend fun getDeliveryNote(id: Int): DeliveryNoteResponse {
        return client.get("delivery-notes/$id") {
            addAuthHeader()
        }.body()
    }

    suspend fun createDeliveryNote(companyId: Int, request: DeliveryNoteRequest): DeliveryNoteResponse {
        return client.post("delivery-notes") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            parameter("company_id", companyId)
            setBody(request)
        }.body()
    }

    suspend fun deleteDeliveryNote(id: Int) {
        client.delete("delivery-notes/$id") {
            addAuthHeader()
        }
    }

    // GRNs (Goods Received Notes)
    suspend fun getGrns(companyId: Int): GrnsResponse {
        return client.get("grns") {
            addAuthHeader()
            parameter("company_id", companyId)
        }.body()
    }

    suspend fun getGrn(id: Int): GrnResponse {
        return client.get("grns/$id") {
            addAuthHeader()
        }.body()
    }

    suspend fun createGrn(companyId: Int, request: GrnRequest): GrnResponse {
        return client.post("grns") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            parameter("company_id", companyId)
            setBody(request)
        }.body()
    }

    suspend fun deleteGrn(id: Int) {
        client.delete("grns/$id") {
            addAuthHeader()
        }
    }

    suspend fun updateDeliveryNote(id: Int, request: DeliveryNoteRequest): DeliveryNoteResponse {
        return client.put("delivery-notes/$id") {
            addAuthHeader()
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateGrn(id: Int, request: GrnRequest): GrnResponse {
        return client.put("grns/$id") {
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
