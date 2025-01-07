package com.prac.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key")
data class RemoteKeyEntity(
    @PrimaryKey val repoId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)