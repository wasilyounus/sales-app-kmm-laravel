package com.sales.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_timestamps")
data class SyncTimestampEntity(
    @PrimaryKey val key: String,
    val timestamp: String,
    val lastSyncedAt: Long
)
