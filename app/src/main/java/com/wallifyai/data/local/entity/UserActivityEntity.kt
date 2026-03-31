package com.wallifyai.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_activity")
data class UserActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "image_id")
    val imageId: String,
    val category: String,
    @ColumnInfo(name = "action_type")
    val actionType: String,
    val timestamp: Long,
)

