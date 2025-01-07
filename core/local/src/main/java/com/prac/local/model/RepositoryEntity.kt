package com.prac.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repository")
data class RepositoryEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @Embedded val owner: OwnerEntity,
    @ColumnInfo(name = "stargazersCount") val stargazersCount: Int,
    @ColumnInfo(name = "updatedAt") val updatedAt: String,
    @ColumnInfo(name = "defaultBranch") val defaultBranch: String,
    @ColumnInfo(name = "isStarred") val isStarred: Boolean?
)
