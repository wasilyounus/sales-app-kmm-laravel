package com.sales.app.presentation.navigation

import kotlinx.serialization.Serializable

// Type-safe navigation routes for Navigation Compose 2.9+
// Using @Serializable for compile-time type safety

// Simple screens (no arguments)
@Serializable object Login
@Serializable object Register
@Serializable object Home
@Serializable object Items
@Serializable object ItemCreate
@Serializable object Parties
@Serializable object PartyCreate
@Serializable object Quotes
@Serializable object QuoteCreate
@Serializable object Orders
@Serializable object OrderCreate
@Serializable object Sales
@Serializable object SaleCreate
@Serializable object Purchases
@Serializable object PurchaseCreate
@Serializable object Inventory
@Serializable object Payments
@Serializable object PaymentCreate
@Serializable object PriceLists
@Serializable object PriceListCreate
@Serializable object DeliveryNotes
@Serializable object Grns
@Serializable object Settings
@Serializable object CompanySettings

// Screens with arguments
@Serializable data class ItemEdit(val itemId: Int)
@Serializable data class PartyEdit(val partyId: Int)
@Serializable data class QuoteEdit(val quoteId: Int)
@Serializable data class QuoteDetail(val quoteId: Int)
@Serializable data class OrderEdit(val orderId: Int)
@Serializable data class OrderDetail(val orderId: Int)
@Serializable data class SaleEdit(val saleId: Int)
@Serializable data class SaleDetail(val saleId: Int)
@Serializable data class PurchaseEdit(val purchaseId: Int)
@Serializable data class PurchaseDetail(val purchaseId: Int)
@Serializable data class PriceListDetail(val priceListId: Long)

// Backward compatibility: Keep old Screen class for gradual migration
// TODO: Remove once all usages are updated
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
    object Payments : Screen("payments")
    object PaymentCreate : Screen("payments/create")
    object PriceLists : Screen("price-lists")
    object PriceListCreate : Screen("price-lists/create")
    object PriceListDetail : Screen("price-lists/{priceListId}") {
        fun createRoute(priceListId: Long) = "price-lists/$priceListId"
    }
    object Settings : Screen("settings")
    object CompanySettings : Screen("account-settings")
}
