package com.sales.app.domain.model

data class Grn(
    val id: Int,
    val purchaseId: Int,
    val grnNo: String?,
    val date: String,
    val vehicleNo: String?,
    val invoiceNo: String?,
    val notes: String?,
    val companyId: Int,
    val items: List<GrnItem> = emptyList()
)

data class GrnItem(
    val id: Int,
    val grnId: Int,
    val itemId: Int,
    val quantity: Double,
    val itemName: String? = null
)
