package com.prac.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
data class OwnerEntity(
    @ColumnInfo(name = "login") val login: String,
    @ColumnInfo(name = "avatarUrl") val avatarUrl: String
)