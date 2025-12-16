package com.sales.app.domain.model

data class DeliveryNote(
    val id: Int,
    val saleId: Int,
    val dnNo: String?,
    val date: String,
    val vehicleNo: String?,
    val lrNo: String?,
    val notes: String?,
    val companyId: Int,
    val items: List<DeliveryNoteItem> = emptyList()
)

data class DeliveryNoteItem(
    val id: Int,
    val deliveryNoteId: Int,
    val itemId: Int,
    val quantity: Double,
    val itemName: String? = null
)
