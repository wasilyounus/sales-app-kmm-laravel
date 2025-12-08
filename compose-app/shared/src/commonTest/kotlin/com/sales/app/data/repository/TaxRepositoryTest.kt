package com.sales.app.data.repository

import com.sales.app.data.local.dao.TaxDao
import com.sales.app.data.local.entity.TaxEntity
import com.sales.app.data.remote.ApiService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TaxRepositoryTest {

    private lateinit var taxDao: TaxDao
    private lateinit var apiService: ApiService
    private lateinit var repository: TaxRepositoryImpl

    @BeforeTest
    fun setup() {
        taxDao = mockk()
        apiService = mockk()
        repository = TaxRepositoryImpl(apiService, taxDao)
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `getAllActiveTaxes returns mapped domain models`() = runTest {
        // Given
        val taxEntity = TaxEntity(
            id = 1,
            schemeName = "GST",
            country = "India",
            tax1Name = "CGST",
            tax1Val = 9.0,
            tax2Name = "SGST",
            tax2Val = 9.0,
            tax3Name = null,
            tax3Val = null,
            tax4Name = null,
            tax4Val = null,
            active = true,
            createdAt = "2024-01-01",
            updatedAt = "2024-01-01"
        )
        every { taxDao.getAllActive() } returns flowOf(listOf(taxEntity))

        // When
        val result = repository.getAllActiveTaxes().first()

        // Then
        assertEquals(1, result.size)
        val tax = result.first()
        assertEquals(1, tax.id)
        assertEquals("GST", tax.schemeName)
        assertEquals("India", tax.country)
        assertEquals(9.0, tax.tax1Val)
    }

    @Test
    fun `getActiveTaxesByCountry filters correctly`() = runTest {
        // Given
        val indiaTax = TaxEntity(
            id = 1, schemeName = "GST", country = "India",
            tax1Name = "CGST", tax1Val = 9.0, tax2Name = "SGST", tax2Val = 9.0,
            tax3Name = null, tax3Val = null, tax4Name = null, tax4Val = null,
            active = true, createdAt = "", updatedAt = ""
        )
        val globalTax = TaxEntity(
            id = 2, schemeName = "No Tax", country = null,
            tax1Name = null, tax1Val = 0.0, tax2Name = null, tax2Val = 0.0,
            tax3Name = null, tax3Val = null, tax4Name = null, tax4Val = null,
            active = true, createdAt = "", updatedAt = ""
        )
        val usTax = TaxEntity(
            id = 3, schemeName = "Sales Tax", country = "US",
            tax1Name = "State", tax1Val = 5.0, tax2Name = null, tax2Val = null,
            tax3Name = null, tax3Val = null, tax4Name = null, tax4Val = null,
            active = true, createdAt = "", updatedAt = ""
        )

        every { taxDao.getAllActive() } returns flowOf(listOf(indiaTax, globalTax, usTax))

        // When
        val result = repository.getActiveTaxesByCountry("India").first()

        // Then
        assertEquals(2, result.size) // India + Global
        assertEquals(1, result.find { it.id == 1 }?.id)
        assertEquals(2, result.find { it.id == 2 }?.id)
        assertEquals(null, result.find { it.id == 3 })
    }
}
