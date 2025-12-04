package com.sales.app.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Items : Screen("items")
    object ItemCreate : Screen("items/create")
    object ItemEdit : Screen("items/edit/{itemId}") {
        fun createRoute(itemId: Int) = "items/edit/$itemId"
    }
    object Parties : Screen("parties")
    object PartyCreate : Screen("parties/create")
    object PartyEdit : Screen("parties/edit/{partyId}") {
        fun createRoute(partyId: Int) = "parties/edit/$partyId"
    }
    object Quotes : Screen("quotes")
    object QuoteCreate : Screen("quotes/create")
    object QuoteEdit : Screen("quotes/edit/{quoteId}") {
        fun createRoute(quoteId: Int) = "quotes/edit/$quoteId"
    }
    object QuoteDetail : Screen("quotes/{quoteId}") {
        fun createRoute(quoteId: Int) = "quotes/$quoteId"
    }
    object Orders : Screen("orders")
    object OrderCreate : Screen("orders/create")
    object OrderEdit : Screen("orders/edit/{orderId}") {
        fun createRoute(orderId: Int) = "orders/edit/$orderId"
    }
    object OrderDetail : Screen("orders/{orderId}") {
        fun createRoute(orderId: Int) = "orders/$orderId"
    }
    object Sales : Screen("sales")
    object SaleCreate : Screen("sales/create")
    object SaleEdit : Screen("sales/edit/{saleId}") {
        fun createRoute(saleId: Int) = "sales/edit/$saleId"
    }
    object SaleDetail : Screen("sales/{saleId}") {
        fun createRoute(saleId: Int) = "sales/$saleId"
    }
    object Purchases : Screen("purchases")
    object PurchaseCreate : Screen("purchases/create")
    object PurchaseEdit : Screen("purchases/edit/{purchaseId}") {
        fun createRoute(purchaseId: Int) = "purchases/edit/$purchaseId"
    }
    object PurchaseDetail : Screen("purchases/{purchaseId}") {
        fun createRoute(purchaseId: Int) = "purchases/$purchaseId"
    }
    object Inventory : Screen("inventory")
    object StockAdjustment : Screen("inventory/adjustment/{accountId}") {
        fun createRoute(accountId: Int) = "inventory/adjustment/$accountId"
    }
    object Settings : Screen("settings")
    object AccountSettings : Screen("account-settings")
}
