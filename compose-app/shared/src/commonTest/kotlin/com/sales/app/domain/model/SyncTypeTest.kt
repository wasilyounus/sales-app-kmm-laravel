package com.sales.app.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class SyncTypeTest {
    
    @Test
    fun `expand Items should return Items`() {
        val result = SyncType.expand(listOf(SyncType.Items))
        assertEquals(1, result.size)
        assertTrue(result.contains(SyncType.Items))
    }

    @Test
    fun `expand Sales should return Sales`() {
        val result = SyncType.expand(listOf(SyncType.Sales))
        assertEquals(1, result.size)
        assertTrue(result.contains(SyncType.Sales))
    }

    @Test
    fun `expand AllMasterData should return all master data types`() {
        val result = SyncType.expand(listOf(SyncType.AllMasterData))
        
        assertTrue(result.contains(SyncType.Items))
        assertTrue(result.contains(SyncType.Parties))
        assertTrue(result.contains(SyncType.Taxes))
        assertTrue(result.contains(SyncType.Uqcs))
        
        assertFalse(result.contains(SyncType.Sales))
        assertFalse(result.contains(SyncType.Quotes))
    }

    @Test
    fun `expand Everything should return all types`() {
        val result = SyncType.expand(listOf(SyncType.Everything))
        
        // Master Data
        assertTrue(result.contains(SyncType.Items))
        assertTrue(result.contains(SyncType.Parties))
        assertTrue(result.contains(SyncType.Taxes))
        assertTrue(result.contains(SyncType.Uqcs))
        
        // Transaction Data
        assertTrue(result.contains(SyncType.Sales))
        assertTrue(result.contains(SyncType.Quotes))
        assertTrue(result.contains(SyncType.Purchases))
        assertTrue(result.contains(SyncType.Orders))
        assertTrue(result.contains(SyncType.Payments))
        assertTrue(result.contains(SyncType.PriceLists))
    }
    
    @Test
    fun `expand mixed types should combine correctly`() {
        val result = SyncType.expand(listOf(SyncType.Items, SyncType.Sales))
        
        assertEquals(2, result.size)
        assertTrue(result.contains(SyncType.Items))
        assertTrue(result.contains(SyncType.Sales))
    }
    
    @Test
    fun `expand with AllMasterData and specific type should dedup`() {
        // Items is implicitly part of AllMasterData
        val result = SyncType.expand(listOf(SyncType.AllMasterData, SyncType.Items))
        
        // Count should be count of AllMasterData components
        val allMasterDataCount = SyncType.expand(listOf(SyncType.AllMasterData)).size
        assertEquals(allMasterDataCount, result.size)
    }
}
