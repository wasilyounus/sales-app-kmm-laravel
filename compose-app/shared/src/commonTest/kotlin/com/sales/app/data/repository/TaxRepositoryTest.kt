package com.sales.app.data.repository

import com.sales.app.data.local.dao.TaxDao
import com.sales.app.data.local.entity.TaxEntity
import com.sales.app.data.remote.ApiService
import com.sales.app.domain.model.Tax
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class TaxRepositoryTest {

    private lateinit var taxDao: TaxDao
    private lateinit var apiService: ApiService
    private lateinit var repository: TaxRepository

    @BeforeTest
    fun setup() {
        taxDao = mockk()
        apiService = mockk()
        repository = TaxRepository(apiService, taxDao)
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `getAllActiveTaxes returns active taxes from DAO`() = runTest {
        // Given
        val taxEntities = listOf(
            TaxEntity(
                id = 1,
                schemeName = "GST 18%",
                tax1Name = "CGST",
                tax1Val = 9.0,
                tax2Name = "SGST",
                tax2Val = 9.0,
                tax3Name = null,
                tax3Val = null,
                tax4Name = null,
                tax4Val = null,
                active = true,
                logId = 1
            ),
            TaxEntity(
                id = 2,
                schemeName = "GST 5%",
                tax1Name = "CGST",
                tax1Val = 2.5,
                tax2Name = "SGST",
                tax2Val = 2.5,
                tax3Name = null,
                tax3Val = null,
                tax4Name = null,
                tax4Val = null,
                active = true,
                logId = 1
            )
        )
        
        every { taxDao.getAllActive() } returns flowOf(taxEntities)

        // When
        val result = repository.getAllActiveTaxes().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("GST 18%", result[0].name)
        assertEquals("GST 5%", result[1].name)
        verify(exactly = 1) { taxDao.getAllActive() }
    }

    @Test
    fun `getAllActiveTaxes returns empty list when no taxes`() = runTest {
        // Given
        every { taxDao.getAllActive() } returns flowOf(emptyList())

        // When
        val result = repository.getAllActiveTaxes().first()

        // Then
        assertTrue(result.isEmpty())
        verify(exactly = 1) { taxDao.getAllActive() }
    }

    @Test
    fun `getAllTaxes returns all taxes including inactive`() = runTest {
        // Given
        val taxEntities = listOf(
            TaxEntity(1, "GST 18%", "CGST", 9.0, "SGST", 9.0, null, null, null, null, true, 1),
            TaxEntity(2, "GST 28%", "CGST", 14.0, "SGST", 14.0, null, null, null, null, false, 1)
        )
        
        every { taxDao.getAll() } returns flowOf(taxEntities)

        // When
        val result = repository.getAllTaxes().first()

        // Then
        assertEquals(2, result.size)
        verify(exactly = 1) { taxDao.getAll() }
    }

    @Test
    fun `tax entity maps correctly to domain model`() = runTest {
        // Given
        val taxEntity = TaxEntity(
            id = 1,
            schemeName = "VAT 15%",
            tax1Name = "VAT",
            tax1Val = 15.0,
            tax2Name = null,
            tax2Val = null,
            tax3Name = null,
            tax3Val = null,
            tax4Name = null,
            tax4Val = null,
            active = true,
            logId = 1
        )
        
        every { taxDao.getAllActive() } returns flowOf(listOf(taxEntity))

        // When
        val result = repository.getAllActiveTaxes().first()

        // Then
        val tax = result.first()
        assertEquals(1, tax.id)
        assertEquals("VAT 15%", tax.name)
        assertEquals("VAT", tax.tax1Name)
        assertEquals(15.0, tax.tax1Rate)
        assertNull(tax.tax2Name)
        assertTrue(tax.active)
    }
}
