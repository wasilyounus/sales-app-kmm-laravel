package com.sales.app.util

expect fun isDesktop(): Boolean

data class PrintItem(
    val name: String,
    val qty: String,
    val price: String,
    val total: String
)

data class PrintData(
    val title: String,
    val subtitle: String,
    val items: List<PrintItem>,
    val total: String,
    val meta: Map<String, String> = emptyMap()
)

interface PlatformShare {
    fun shareText(text: String)
    fun sharePdf(data: PrintData)
    fun print(data: PrintData)
}

expect fun getPlatformShare(): PlatformShare
