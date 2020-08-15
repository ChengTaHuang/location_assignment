package com.cartrack.assignment.data.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class Account(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long = 1,
    @ColumnInfo(name = "user_name")
    val userName: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "country_code")
    val countryCode : String
)